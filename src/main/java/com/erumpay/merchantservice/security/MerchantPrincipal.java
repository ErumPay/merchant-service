package com.erumpay.merchantservice.security;

public record MerchantPrincipal(
        Long accountId,
        Long merchantId,
        String role
) {
}
