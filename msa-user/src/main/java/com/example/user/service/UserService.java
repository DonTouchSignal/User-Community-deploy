package com.example.user.service;

import com.example.user.dto.UserDto;
import com.example.user.entities.UserEntity;
import com.example.user.entities.SubsEntity;
import com.example.user.repository.UserRepository;
import com.example.user.repository.SubsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 회원관련 비즈니스 로직 처리
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubsRepository subsRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JavaMailSender mailSender; // 자바 메일 전송 라이브러리

    @Transactional
    public void signup(UserDto userDto) {
        // 1. 입력값 검증
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일은 입력해주세요");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이메일이 이미 존재합니다");
        }
        if (userDto.getNickName() == null || userDto.getNickName().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 넣어주세요");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 넣어주세요");
        }

        // 2. 사용자 엔티티 생성
        UserEntity userEntity = UserEntity.builder()
                .nickName(userDto.getNickName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role("ROLE_USER")
                .enable(false)
                .build();

        // 3. 사용자 저장
        userRepository.save(userEntity);

        // 4. 구독 정보 생성 (nickName 제거)
        SubsEntity subsEntity = new SubsEntity();
        subsEntity.setEmail(userDto.getEmail());
        subsEntity.setSubscriptionStatus(false);  // 초기 구독 상태는 false
        subsRepository.save(subsEntity);

        // 5. 인증 이메일 발송
        sendVaildEmail(userEntity);
    }
    // 이메일 전송 메소드
    private void sendVaildEmail(UserEntity userEntity) {
        // 이메일 내용 안에 인증 요청을 GET방식으로 요청하도록 URL을 구성
        // 게이트웨이에 프리패스로 URL 등록되어야 한다
        // URL 합당하게 처리되기 위해서 토큰(일종의)값 같이 전달
        // 일종의 토큰(고유값)을 발급 -> 레디스에 저장(이메일 기준), 유저하게 발송
        // 유저는 이메이르 확인 -> 클릭 -> 게이트웨이->서비스진입 -> 전달된 토큰과 레디스에 저장된 토큰 일치 -> 인증
        // 필요시 레디스 저장할때 만료시간 지정 or 토큰 자체에 만료시간 적용 할수 있다

        // 1. 토큰 발행 -> 단순하게 기기 고유값 생성
        String token = UUID.randomUUID().toString();
        // 2. redis 저장 -> 키는 토큰(외부공개->가입자에게만 전달), 값은 이메일(토큰->이메일추출->db에 존재하는가?), 만료시간 6시간,
        //    이메일 인증절차 -> 유효한 이메일인지를 검증하는 단계
        redisTemplate.opsForValue().set(token,
                userEntity.getEmail(), 6, TimeUnit.HOURS);
        // 3. URL 구성 -> 가입한 사용자의 이메일에서 인증메일에 전송된 링크
        String url = "http://localhost:8080/user/vaild?token=" + token;
        // 4. 메일 전송 (받는 사람주소, 제목, 내용)
        sendMail( userEntity.getEmail(), "Email 인증", "링크를 눌러서 인증: " + url );
    }
    private void sendMail(String email, String subject, String content) {
        // 1. 메세지 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        // 2. 전송
        mailSender.send(message);
    }
    // enable 컬럼 : f->t (유효할때만)
    public void updateActivate(String token) {
        // 1. 레디스 토큰 -> 이메일 획득
        String email = (String) redisTemplate.opsForValue().get(token);
        // 2. 없다면 -> 잘못된 토큰 혹은 만료된 토큰 -> 예외 처리(토큰오류)
        if(email == null) {
            throw new IllegalArgumentException("잘못된 토큰 혹은 만료된 토큰");
        }
        // 3. 존재한다면 -> 이메일(id, pk)  -> 엔티티 획득
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow( ()-> new IllegalArgumentException("사용자 오류(존재x)") );
        // 4. enable 컬럼 : f->t => 저장
        userEntity.setEnable(true);
        userRepository.save(userEntity);
        // 5. 레디스 토큰 삭제
        redisTemplate.delete(token);
    }

    public String getNicknameByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return userEntity.getNickName();  // UserEntity의 필드명이 nickName임을 확인
    }
}