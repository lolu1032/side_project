package com.example.sideProject.payment.exception;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.sideProject.exception.PromotionErrorCode;

public class ProductException extends CustomException {

    public ProductException() {
        super(ProductErrorCode.DEFAULT);
    }

    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProductException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ProductException(Throwable cause) {
        super(cause);
    }
}
