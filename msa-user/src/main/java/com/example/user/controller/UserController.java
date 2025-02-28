package com.example.user.controller;

import com.example.user.dto.UserDto;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// 게이트웨이에서 연동되어 있음
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/signup")
    // ResponseEntity<?>  => 응답을 유연하게 구성 가능함
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {
        System.out.println("회원가입요청 : " + userDto.toString());
        // 1. 회원가입처리 -> 비즈니스로직 -> 서비스 해결
        userService.signup(userDto);
        // 2. 응답처리
        return ResponseEntity.ok("회원가입 성공");
    }
    // 이메일 검증 처리 -> 인증메일 처리 -> enable의 값을 f->t
    // GET, /user/vaild, (*)엑세스토큰없이 전근가능해야함(로그인전->게이트웨이 수정), 파라미터 token,
    @GetMapping("/vaild")
    public ResponseEntity<String> vaild(@RequestParam("token") String token) {
        try{
            // 고객 테이블 업데이트 - enable : f->t (유효할때만)
            userService.updateActivate( token );
            return ResponseEntity.ok("이메일 인증 완료. 계정이 활성화 되었습니다.");
        } catch (IllegalArgumentException e) {
            // 조작된(만료된) 토큰을 인증 -> 비정상, Bad Request
            return ResponseEntity.status(400).body("비정상, Bad Request : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버측 내부 오류 : " + e.getMessage());
        }
    }

    @GetMapping("/nickname")
    public ResponseEntity<String> getNickname(@RequestHeader("X-Auth-User") String email) {
        try {
            String nickname = userService.getNicknameByEmail(email);
            return ResponseEntity.ok(nickname);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("사용자 정보 조회 실패: " + e.getMessage());
        }
    }
}