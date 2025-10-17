package com.example.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{

    protected /*final*/ ErrorCode errorCode;
    public CustomException() {
        super();
        this.errorCode = DefaultErrorCodeHolder.INSTANCE;
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    public CustomException(Throwable cause) {
        super(cause);
        this.errorCode = DefaultErrorCodeHolder.INSTANCE;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }


    // 내부 클래스의 클래스 로드타임 스레드 세이프 활용하기 + 필요할때 로드
    private static class DefaultErrorCodeHolder {
        private static final ErrorCode INSTANCE = new ErrorCode() { // 익명 클래스 (익명 객체)
            // 즉성에서 상속 또는 구현받아서 인스턴스 만들기
            @Override
            public String name() {
                return "SEVER_ERROR";
            }

            @Override
            public String message() {
                return "서버 오류";
            }

            @Override
            public HttpStatus status() {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public RuntimeException exception() {
                return new CustomException(this);
            }

            @Override
            public RuntimeException exception(Throwable cause) {
                return new CustomException(this,cause);
            }
        };
    }
}
