package com.example.sideProject.payment.exception;

import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    DEFAULT("서버 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("상품을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    INSUFFICIENT_FUNDS("잔액 부족",HttpStatus.UNPROCESSABLE_ENTITY);
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
        return new ProductException(this);
    }

    @Override
    public RuntimeException exception(Throwable cause) {
        return new ProductException(this, cause);
    }
}
