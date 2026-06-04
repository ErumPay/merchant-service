package com.erumpay.merchantservice.global.exception;

import lombok.Getter;

@Getter
public class MerchantException extends RuntimeException {

    private final ErrorCode errorCode;

    public MerchantException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MerchantException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MerchantException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public MerchantException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
