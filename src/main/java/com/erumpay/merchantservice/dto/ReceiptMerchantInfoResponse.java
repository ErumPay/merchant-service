package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;

public record ReceiptMerchantInfoResponse (
        Long merchantId,
        String merchantName,
        String businessNumber,
        String ownerName,
        String businessAddress,
        String contactPhone
){
    public static ReceiptMerchantInfoResponse from(Merchant merchant){
        return new ReceiptMerchantInfoResponse(
                merchant.getMerchantId(),
                merchant.getMerchantName(),
                merchant.getBusinessNumber(),
                merchant.getOwnerName(),
                merchant.getBusinessAddress(),
                merchant.getContactPhone()
        );
    }
}
