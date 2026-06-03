package com.erumpay.merchantservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record InternalMerchantCreateRequest(
        @JsonProperty("business_number")
        String businessNumber,

        @JsonProperty("merchant_name")
        String merchantName,

        @JsonProperty("mcc_code")
        String mccCode,

        @JsonProperty("representative_name")
        String representativeName,

        @JsonProperty("contact_email")
        String contactEmail,

        @JsonProperty("contact_phone")
        String contactPhone,

        @JsonProperty("settlement_account")
        String settlementAccount,

        @JsonProperty("bank_name")
        String bankName,

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
