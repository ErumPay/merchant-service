package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;

public record MerchantValidationResponse (
        Long merchantId,
        boolean valid,
        MerchantStatus status,
        ApiKeyStatus apiKeyStatus
) {
    public static MerchantValidationResponse from(Merchant merchant){
        boolean valid;

        if (merchant.getStatus() == MerchantStatus.ACTIVE
                && merchant.getApiKeyStatus() == ApiKeyStatus.ACTIVE) {
            valid = true;
        } else {
            valid = false;
        }

        return new MerchantValidationResponse(
                merchant.getMerchantId(),
                valid,
                merchant.getStatus(),
                merchant.getApiKeyStatus()
        );
    }
}
