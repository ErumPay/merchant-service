package com.erumpay.merchantservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApiKeyValidationRequest(
        @NotBlank
        @Size(max = 255)
        String apiKey
) {
}