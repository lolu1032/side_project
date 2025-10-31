package com.example.sideProject.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PROCESSING("결재 진행 중입니다."),
    COMPLETED("결제 성공"),
    FAILED("결재 실패");
    private String status;

    PaymentStatus(String s) {
        this.status = s;
    }
}
