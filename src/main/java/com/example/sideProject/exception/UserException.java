package com.example.sideProject.exception;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;

public class UserException extends CustomException {

    public UserException() {
        super(UserErrorCode.DEFAULT);
    }

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }
}
