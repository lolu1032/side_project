package com.example.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name(); // enum 클래스에는 모두 자동으로 구현됨
    String message();
    HttpStatus status();
    RuntimeException exception();
    RuntimeException exception(Throwable cause);
}