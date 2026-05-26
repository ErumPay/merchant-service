package com.erumpay.merchantservice.dto;

import jakarta.validation.constraints.NotBlank;

public record ApiKeyValidationRequest(
        @NotBlank
        String apiKey
) {
}