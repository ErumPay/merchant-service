package com.erumpay.merchantservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalMerchantCreateResponse(
        @JsonProperty("merchant_id")
        Long merchantId,

        @JsonProperty("review_status")
        String reviewStatus
) {

    public static InternalMerchantCreateResponse from(MerchantResponse response) {
        return new InternalMerchantCreateResponse(response.merchantId(), "WAITING");
    }
}
