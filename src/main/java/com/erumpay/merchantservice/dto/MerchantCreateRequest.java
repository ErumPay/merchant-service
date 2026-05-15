package com.erumpay.merchantservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantCreateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 30)
        String businessNumber,

        @NotBlank
        @Size(max = 100)
        String ownerName,

        @NotBlank
        @Size(max = 20)
        String phoneNumber
) {
}
