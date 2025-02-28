package com.example.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * 결제 요청 정보를 가진 dto
 */
@Data
public class PaymentDto {
    @NotNull
    private String email;

    // 실제는 더 많은 정보가 필요할 수 있다
    @Builder
    public PaymentDto(String email) {
        this.email = email;
    }
    // getMsg() 메서드
    public String getMsg() {
        return String.format("PaymentDto { email='%s'}", email);
    }
}
