package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;

import java.time.LocalDateTime;

public record ApiKeyRotateResponse (
        Long merchantId,
        String apiKey,
        ApiKeyStatus apiKeyStatus,
        LocalDateTime apiKeyRotatedAt
) {
    public static ApiKeyRotateResponse from(Merchant merchant){
        return new ApiKeyRotateResponse(
                merchant.getMerchantId(),
                merchant.getApiKey(),
                merchant.getApiKeyStatus(),
                merchant.getApiKeyRotatedAt()
        );
    }
}
