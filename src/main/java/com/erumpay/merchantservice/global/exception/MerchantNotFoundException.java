package com.erumpay.merchantservice.global.exception;

public class MerchantNotFoundException extends MerchantException {

    public MerchantNotFoundException(String message) {
        super(ErrorCode.MERCHANT_NOT_FOUND, message);
    }
}
