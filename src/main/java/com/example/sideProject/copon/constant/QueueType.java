package com.example.sideProject.copon.constant;

public enum QueueType {
    WAITING("대기열에 있으며 순번 대기 중"),
    PROCESSING("발급 처리 중"),
    COMPLETED("발급 성공"),
    FAILED("발급 실패 (재고 부족 등)"),
    ;

    private final String message;

    QueueType(String message) {
        this.message = message;
    }
    public String getDescription() {
        return message;
    }
}
