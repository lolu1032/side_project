package com.example.sideProject.exception;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;

public class PromotionException extends CustomException {

    public PromotionException() {
        super(PromotionErrorCode.DEFAULT);
    }

    public PromotionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PromotionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public PromotionException(Throwable cause) {
        super(cause);
    }
}
