package com.erumpay.merchantservice.controller;

import com.erumpay.merchantservice.dto.*;
import com.erumpay.merchantservice.entity.MerchantStatusHistory;
import com.erumpay.merchantservice.service.MerchantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/merchants")
public class InternalMerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public InternalMerchantCreateResponse createInternalMerchant(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody InternalMerchantCreateRequest request
    ) {
        MerchantResponse response = merchantService.createMerchant(request.toMerchantCreateRequest());
        return InternalMerchantCreateResponse.from(response);
    }

    @GetMapping("/{merchantId}")
    public InternalMerchantResponse getInternalMerchant(@PathVariable Long merchantId) {
        return merchantService.getInternalMerchant(merchantId);
    }

    @GetMapping("/{merchantId}/validate")
    public MerchantValidationResponse validateMerchant(@PathVariable Long merchantId) {
        return merchantService.validateMerchant(merchantId);
    }

    @GetMapping("/{merchantId}/settlement-policy")
    public SettlementPolicyResponse getSettlementPolicy(@PathVariable Long merchantId) {
        return merchantService.getSettlementPolicy(merchantId);
    }

    @GetMapping("/{merchantId}/receipt-info")
    public ReceiptMerchantInfoResponse getReceiptMerchantInfo(@PathVariable Long merchantId) {
        return merchantService.getReceiptMerchantInfo(merchantId);
    }

    @PostMapping("/api-key/validate")
    public ApiKeyValidationResponse validateApiKey(@Valid @RequestBody ApiKeyValidationRequest request) {
        return merchantService.validateApiKey(request);
    }
}
