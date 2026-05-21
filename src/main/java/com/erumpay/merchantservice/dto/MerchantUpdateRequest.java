package com.erumpay.merchantservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MerchantUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String merchantName,

        @NotBlank
        @Size(max = 50)
        String ownerName,

        @NotBlank
        @Size(max = 20)
        String contactPhone,

        @NotBlank
        @Size(max = 255)
        String businessAddress,

        @NotBlank
        @Size(max = 50)
        String categoryName,

        @NotBlank
        @Size(min = 4, max = 4)
        @Pattern(regexp = "\\d{4}", message = "MCC 코드는 숫자 4자리여야 합니다.")
        String mccCode,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal feeRate,

        @NotBlank
        @Size(max = 100)
        String settlementAccount
) {
}