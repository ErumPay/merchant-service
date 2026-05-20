package com.erumpay.merchantservice.global.exception;

public class DuplicateMerchantException extends RuntimeException {

    public DuplicateMerchantException(String message) {
        super(message);
    }

    public DuplicateMerchantException(String message, Throwable cause) {
        super(message, cause);
    }
}