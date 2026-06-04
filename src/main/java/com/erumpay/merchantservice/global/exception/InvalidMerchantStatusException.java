package com.erumpay.merchantservice.global.exception;

public class InvalidMerchantStatusException extends MerchantException {

    public InvalidMerchantStatusException(String message) {
        super(ErrorCode.INVALID_MERCHANT_STATUS, message);
    }

    public InvalidMerchantStatusException(String message, Throwable cause) {
        super(ErrorCode.INVALID_MERCHANT_STATUS, message, cause);
    }
}
