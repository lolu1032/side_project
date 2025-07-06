package com.example.side_project.exception;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;

public class CouponException extends CustomException {
    public CouponException() {
        super(CouponErrorCode.DEFAULT);
    }

    public CouponException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CouponException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public CouponException(Throwable cause) {
        super(cause);
    }
}
