package com.erumpay.merchantservice.global.exception;

public class MerchantAlreadyDeletedException extends MerchantException {

    public MerchantAlreadyDeletedException(String message) {
        super(ErrorCode.MERCHANT_ALREADY_DELETED, message);
    }

    public MerchantAlreadyDeletedException(String message, Throwable cause) {
        super(ErrorCode.MERCHANT_ALREADY_DELETED, message, cause);
    }
}
