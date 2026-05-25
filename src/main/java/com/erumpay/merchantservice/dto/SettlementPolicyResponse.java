package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.MerchantStatus;

import java.math.BigDecimal;

public record SettlementPolicyResponse (
        Long merchantId,
        BigDecimal feeRate,
        String settlementAccount,
        MerchantStatus status
) {
    public static SettlementPolicyResponse from(Merchant merchant) {
        return new SettlementPolicyResponse(
                merchant.getMerchantId(),
                merchant.getFeeRate(),
                merchant.getSettlementAccount(),
                merchant.getStatus()
        );
    }
}
