package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;

public record ApiKeyValidationResponse(
        Long merchantId,
        boolean valid,
        MerchantStatus merchantStatus,
        ApiKeyStatus apiKeyStatus
) {
    public static ApiKeyValidationResponse valid(Merchant merchant) {
        return new ApiKeyValidationResponse(
                merchant.getMerchantId(),
                true,
                merchant.getStatus(),
                merchant.getApiKeyStatus()
        );
    }
    public static ApiKeyValidationResponse invalid() {
        return new ApiKeyValidationResponse(
                null,
                false,
                null,
                null
        );
    }
}