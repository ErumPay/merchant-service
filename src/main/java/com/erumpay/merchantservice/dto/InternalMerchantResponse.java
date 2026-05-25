package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;

import java.math.BigDecimal;

public record InternalMerchantResponse(
        Long merchantId,
        String merchantName,
        String businessNumber,
        ApiKeyStatus apiKeyStatus,
        BigDecimal feeRate,
        MerchantStatus status) {

    public static InternalMerchantResponse from(Merchant merchant) {
        return new InternalMerchantResponse(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getBusinessNumber(),
                merchant.getApiKeyStatus(),
                merchant.getFeeRate(),
                merchant.getStatus()
        );
    }
}
