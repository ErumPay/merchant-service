package com.erumpay.merchantservice.global.exception;

public class IdempotencyKeyRequiredException extends MerchantException {

    public IdempotencyKeyRequiredException(String message) {
        super(ErrorCode.IDEMPOTENCY_KEY_REQUIRED, message);
    }
}
