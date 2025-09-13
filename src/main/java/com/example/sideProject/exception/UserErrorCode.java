package com.example.sideProject.exception;


import com.example.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    DEFAULT("서버 에러", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_USERNAM("이미 존재하는 아이디입니다.",HttpStatus.CONFLICT),
    EMPTY_USERNAME("아이디를 입력하세요.", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME("아이디는 4자 이상 20자 이하로 입력해야 합니다.", HttpStatus.BAD_REQUEST),
    EMPTY_PASSWORD("비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_LENGTH("비밀번호는 8자 이상 30자 이하로 입력해야 합니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NO_NUMBER("비밀번호는 최소 하나 이상의 숫자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NO_LETTER("비밀번호는 최소 하나 이상의 영문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NO_SPECIAL("비밀번호는 최소 하나 이상의 특수문자를 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUNT_USERNAME("아이디가 존재하지않습니다.",HttpStatus.NOT_FOUND),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
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
        return new UserException(this);
    }

    @Override
    public RuntimeException exception(Throwable cause) {
        return new UserException(cause);
    }
}
