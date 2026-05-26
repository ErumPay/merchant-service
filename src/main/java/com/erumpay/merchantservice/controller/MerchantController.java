package com.erumpay.merchantservice.controller;

import com.erumpay.merchantservice.dto.*;
import com.erumpay.merchantservice.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pg-admin/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantResponse createMerchant(@Valid @RequestBody MerchantCreateRequest request) {
        return merchantService.createMerchant(request);
    }

    @GetMapping("/{merchantId}")
    public MerchantResponse getMerchant(@PathVariable Long merchantId) {
        return merchantService.getMerchant(merchantId);
    }

    @GetMapping
    public Page<MerchantResponse> getMerchants(
            @PageableDefault(
                    size = 20,
                    sort = "merchantId",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return merchantService.getMerchants(pageable);
    }

    @PutMapping("/{merchantId}")
    public MerchantResponse updateMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantUpdateRequest request
            ) {
        return merchantService.updateMerchant(merchantId, request);
    }

    @PatchMapping("/{merchantId}/status")
    public MerchantResponse updateMerchantStatus(
            @PathVariable Long merchantId,
            @Valid @RequestBody MerchantStatusUpdateRequest request
    ){
        return merchantService.updateMerchantStatus(merchantId, request);
    }

    @DeleteMapping("/{merchantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMerchant(
            @PathVariable Long merchantId
    ){
        merchantService.deleteMerchant(merchantId);
    }

    @PatchMapping("/{merchantId}/api-key/rotate")
    public ApiKeyRotateResponse rotateApiKey(
            @PathVariable Long merchantId
    ){
        return merchantService.rotateApiKey(merchantId);
    }
}