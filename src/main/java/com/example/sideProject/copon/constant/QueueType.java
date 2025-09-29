package com.example.sideProject.copon.constant;

public enum QueueType {
    WAITING("대기열에 있으며 순번 대기 중"),
    NOT_IN_QUEUE("대기열에 등록되지 않았습니다."),
    COMPLETED("쿠폰 발급이 완료되었습니다."),
    FAILED("쿠폰 재고가 소진되었습니다."),
    PROCESSING("쿠폰 발급 처리 중입니다."),
    ;

    private final String message;

    QueueType(String message) {
        this.message = message;
    }
    public String getDescription() {
        return message;
    }
}
