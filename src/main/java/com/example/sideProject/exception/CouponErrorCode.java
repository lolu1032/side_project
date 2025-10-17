package com.example.sideProject.exception;

import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    DEFAULT("서버 에러",HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND_COUPON("쿠폰이 존재하지 않습니다.",HttpStatus.NOT_FOUND),
    SOLD_OUT_COUPON("쿠폰이 모두 소진됐습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ISSUED_COUPON("이미 모든 유저에게 발급한 쿠폰입니다.", HttpStatus.CONFLICT),
    ISSUED_COUPON("이미 발급된 쿠폰입니다.",HttpStatus.CONFLICT),
    NOT_FOUND_TOKEN("인증 토큰이 없습니다. 로그인 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("인증 토큰이 만료되었습니다.",HttpStatus.UNAUTHORIZED),
    NOT_FOUND_COUPON_OR_USER("쿠폰 또는 유저가 존재하지 않습니다.",HttpStatus.NOT_FOUND),
    USE_COUPON("이미 사용한 쿠폰입니다.",HttpStatus.IM_USED),
    EXPIRED_COUPON("이미 만료된 쿠폰입니다.",HttpStatus.CONFLICT),
    PROMOTION_NOT_ACTIVE("프로모션 기간이 아닙니다.", HttpStatus.BAD_REQUEST),
    COMPLETED("쿠폰 발급이 완료되었습니다.",HttpStatus.OK),
    PROCESSING("쿠폰 발급 처리 중입니다.", HttpStatus.PROCESSING);

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
