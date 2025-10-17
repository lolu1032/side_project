package com.example.sideProject.exception;

import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum QueueErrorCode implements ErrorCode {
    DEFAULT("서버 에러",HttpStatus.INTERNAL_SERVER_ERROR),
    WAITING("대기열에 있으며 순번 대기 중",HttpStatus.OK),
    NOT_IN_QUEUE("대기열에 등록되지 않았습니다.",HttpStatus.BAD_REQUEST)
//    COMPLETED("쿠폰 발급이 완료되었습니다."),
//    FAILED("쿠폰 재고가 소진되었습니다."),
//    PROCESSING("쿠폰 발급 처리 중입니다."),
    ;

    private final String message;
    private final HttpStatus status;

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public RuntimeException exception() {
        return new QueueException(this);
    }

    @Override
    public RuntimeException exception(Throwable cause) {
        return new QueueException(this,cause);
    }
}
