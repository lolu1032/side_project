package com.example.side_project.exception;

import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    NOT_FOUND_COUPON("쿠폰이 존재하지 않습니다.",HttpStatus.NOT_FOUND),
    SOLD_OUT_COUPON("쿠폰이 모두 소진됐습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ISSUED_COUPON("이미 모든 유저에게 발급한 쿠폰입니다.", HttpStatus.CONFLICT),;

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
        return new CouponException(this);
    }

    @Override
    public RuntimeException exception(Throwable cause) {
        return new CouponException(this,cause);
    }
}
