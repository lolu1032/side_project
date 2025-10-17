package com.example.exception;

import com.example.sideProject.exception.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                .instance(path)
                .build();
        return ResponseEntity
                .status(errorCode.status())
                .body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        var fieldError = ex.getBindingResult().getFieldError();
        UserErrorCode errorCode = UserErrorCode.DEFAULT;

        if (fieldError != null) {
            String field = fieldError.getField();
            String code = fieldError.getCode();
            String defaultMsg = fieldError.getDefaultMessage();

            if ("username".equals(field)) {
                if ("NotBlank".equals(code)) errorCode = UserErrorCode.EMPTY_USERNAME;
                else if ("Size".equals(code)) errorCode = UserErrorCode.INVALID_USERNAME;
                else errorCode = UserErrorCode.DEFAULT;

            } else if ("password".equals(field)) {
                if ("NotBlank".equals(code)) errorCode = UserErrorCode.EMPTY_PASSWORD;
                else if ("Size".equals(code)) errorCode = UserErrorCode.INVALID_PASSWORD_LENGTH;
                else if ("Pattern".equals(code)) {
                    if (defaultMsg.contains("숫자")) errorCode = UserErrorCode.PASSWORD_NO_NUMBER;
                    else if (defaultMsg.contains("영문자")) errorCode = UserErrorCode.PASSWORD_NO_LETTER;
                    else if (defaultMsg.contains("특수문자")) errorCode = UserErrorCode.PASSWORD_NO_SPECIAL;
                    else errorCode = UserErrorCode.DEFAULT;
                } else {
                    errorCode = UserErrorCode.DEFAULT;
                }
            } else {
                errorCode = UserErrorCode.DEFAULT;
            }
        }

        ApiError error = ApiError.builder()
                .title(errorCode.message())
                .status(errorCode.status().value())
                .instance(path)
                .build();

        return ResponseEntity.status(errorCode.status()).body(error);
    }
}