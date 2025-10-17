package com.example.sideProject.exception;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;

public class QueueException extends CustomException {
    public QueueException() {
        super(QueueErrorCode.DEFAULT);
    }
    public QueueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public QueueException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public QueueException(Throwable cause) {
        super(cause);
    }

}
