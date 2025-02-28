package com.example.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name="members")
@Data
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails {
    // email -> primary key
    // username, password, role
    // enable (인증여부)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enable; // 이메일 인증 여부

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 생성 시점에 현재 시간으로 초기화

    @Builder
    public UserEntity(String nickName, String email, String password, String role, boolean enable) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enable = enable;
    }

    // ----------------
    // UserDetails 파트 -> 6개 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역활 설정 => ROLE_USER
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // email, password 통해서 로그인 예정
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired();
    }

    // 이메일 인증 여부를 체크
    @Override
    public boolean isEnabled() {
        return enable;//UserDetails.super.isEnabled();
    }
}
