package com.erumpay.merchantservice.global.exception;

public class MerchantAlreadyDeletedException extends RuntimeException {

    public MerchantAlreadyDeletedException(String message) {
        super(message);
    }

    public MerchantAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
