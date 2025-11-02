package com.example.sideProject.exception;

import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PromotionErrorCode implements ErrorCode {
    DEFAULT("서버 오류",HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("찾을 수 없는 프로모션입니다.", HttpStatus.NOT_FOUND);
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
        return new PromotionException(this);
    }

    @Override
    public RuntimeException exception(Throwable cause) {
        return new PromotionException(this,cause);
    }
}
