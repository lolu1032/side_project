package com.example.sideProject.copon.constant;

public final class QueueConstants {
    private QueueConstants() {} // 인스턴스 생성 방지

    // Redis Key 패턴
    public static final String WAITING_QUEUE_KEY = "coupon:waiting_queue:";
    public static final String PROCESSING_SET_KEY = "coupon:processing:";
    public static final String USER_STATUS_KEY = "coupon:user_status:";

    // 배치 처리 설정
    public static final int BATCH_SIZE = 100;
    public static final int SCHEDULE_DELAY_MS = 1000; // 1초

    // 사용자 상태
    public static final String STATUS_WAITING = "WAITING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_NOT_IN_QUEUE = "NOT_IN_QUEUE";
    public static final String STATUS_UNKNOWN = "UNKNOWN";
}