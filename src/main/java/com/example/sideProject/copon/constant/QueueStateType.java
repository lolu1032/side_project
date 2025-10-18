package com.example.sideProject.copon.constant;

import lombok.Getter;

@Getter
public enum QueueStateType {
    WAITING("대기열 %d번으로 등록되었습니다."),
    PROCESSING("이미 쿠폰 발급을 처리 중입니다."),
    COMPLETED("이미 쿠폰을 발급받으셨습니다."),
    FAILED("쿠폰 발급에 실패했습니다."),
    DEFAULT("대기열에 추가되었습니다.");

    private String message;

    QueueStateType(String message) {
        this.message = message;
    }
}
