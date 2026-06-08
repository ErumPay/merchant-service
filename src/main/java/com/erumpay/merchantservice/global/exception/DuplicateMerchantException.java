package com.erumpay.merchantservice.global.exception;

public class DuplicateMerchantException extends MerchantException {

    public DuplicateMerchantException(String message) {
        super(ErrorCode.DUPLICATE_MERCHANT, message);
    }

    public DuplicateMerchantException(String message, Throwable cause) {
        super(ErrorCode.DUPLICATE_MERCHANT, message, cause);
    }
}
