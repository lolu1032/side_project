package com.example.sideProject.payment.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentStatus {
    PROCESSING("결재 진행 중입니다.",HttpStatus.PROCESSING),
    COMPLETED("결제 성공",HttpStatus.OK),
    FAILED("결재 실패",HttpStatus.UNPROCESSABLE_ENTITY);
    private String message;
    private HttpStatus status;

    PaymentStatus(String message,HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
