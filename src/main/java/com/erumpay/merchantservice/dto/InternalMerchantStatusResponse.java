package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.enums.MerchantStatus;

public record InternalMerchantStatusResponse(
        Long merchantId,
        MerchantStatus status
) {
}
