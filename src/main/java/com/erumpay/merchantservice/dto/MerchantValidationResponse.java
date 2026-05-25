package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.MerchantStatus;

public record MerchantValidationResponse (
        Long merchantId,
        boolean valid,
        MerchantStatus status
) {
    public static MerchantValidationResponse from(Merchant merchant){
        boolean valid = merchant.getStatus() == MerchantStatus.ACTIVE ? true : false;

        return new MerchantValidationResponse(
                merchant.getMerchantId(),
                valid,
                merchant.getStatus()
        );
    }
}
