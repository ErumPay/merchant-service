package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;

public record MerchantResponse(
        Long id,
        String name,
        String businessNumber,
        String ownerName,
        String phoneNumber
) {

    public static MerchantResponse from(Merchant merchant) {
        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getBusinessNumber(),
                merchant.getOwnerName(),
                merchant.getPhoneNumber()
        );
    }
}
