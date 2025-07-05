package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiError> handleCustomException(CustomException exception, HttpServletRequest request) {
        var errorCode = exception.getErrorCode();
        var path = request.getRequestURI();

        ApiError error = ApiError.builder()
                .title(errorCode.message())
                .status(errorCode.status().value())
//                .detail()
                .instance(path)
                .build();
//        return new ResponseEntity<>(error, errorCode.status());
        return ResponseEntity
                .status(errorCode.status())
                .body(error);
    }
}