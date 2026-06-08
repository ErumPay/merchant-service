package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.enums.MerchantStatus;
import jakarta.validation.constraints.NotNull;

public record MerchantStatusUpdateRequest(
    @NotNull
    MerchantStatus status,
    String suspendReason
) {

}
