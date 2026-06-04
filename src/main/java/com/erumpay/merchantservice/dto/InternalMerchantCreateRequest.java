package com.erumpay.merchantservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record InternalMerchantCreateRequest(
        @NotBlank
        @Size(max = 20)
        @JsonProperty("business_number")
        String businessNumber,

        @NotBlank
        @Size(max = 100)
        @JsonProperty("merchant_name")
        String merchantName,

        @NotBlank
        @Size(min = 4, max = 4)
        @Pattern(regexp = "\\d{4}", message = "MCC 코드는 숫자 4자리여야 합니다.")
        @JsonProperty("mcc_code")
        String mccCode,

        @NotBlank
        @Size(max = 50)
        @JsonProperty("representative_name")
        String representativeName,

        @Size(max = 255)
        @JsonProperty("contact_email")
        String contactEmail,

        @NotBlank
        @Size(max = 20)
        @JsonProperty("contact_phone")
        String contactPhone,

        @NotBlank
        @Size(max = 100)
        @JsonProperty("settlement_account")
        String settlementAccount,

        @Size(max = 50)
        @JsonProperty("bank_name")
        String bankName,

        @Size(max = 50)
        @JsonProperty("service_name")
        String serviceName
) {

    public MerchantCreateRequest toMerchantCreateRequest() {
        return new MerchantCreateRequest(
                merchantName,
                businessNumber,
                representativeName,
                contactPhone,
                valueOrDefault(contactEmail, "N/A"),
                valueOrDefault(serviceName, "N/A"),
                mccCode,
                BigDecimal.ZERO,
                buildSettlementAccount()
        );
    }

    private String buildSettlementAccount() {
        if (bankName == null || bankName.isBlank()) {
            return settlementAccount;
        }
        if (settlementAccount == null || settlementAccount.isBlank()) {
            return bankName;
        }
        return bankName + " " + settlementAccount;
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
