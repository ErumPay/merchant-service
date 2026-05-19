package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MerchantResponse(
        Long merchantId,
        String merchantName,
        String businessNumber,
        String ownerName,
        String contactPhone,
        String businessAddress,
        String categoryName,
        String mccCode,
        String apiKey,
        ApiKeyStatus apiKeyStatus,
        LocalDateTime apiKeyIssuedAt,
        LocalDateTime apiKeyRotatedAt,
        BigDecimal feeRate,
        String settlementAccount,
        MerchantStatus status,
        String suspendReason,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        LocalDateTime createdAt) {

    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getBusinessNumber(),
                merchant.getOwnerName(),
                merchant.getContactPhone(),
                merchant.getBusinessAddress(),
                merchant.getCategoryName(),
                merchant.getMccCode(),
                merchant.getApiKey(),
                merchant.getApiKeyStatus(),
                merchant.getApiKeyIssuedAt(),
                merchant.getApiKeyRotatedAt(),
                merchant.getFeeRate(),
                merchant.getSettlementAccount(),
                merchant.getStatus(),
                merchant.getSuspendReason(),
                merchant.getUpdatedAt(),
                merchant.getDeletedAt(),
                merchant.getCreatedAt()
        );
    }
}
