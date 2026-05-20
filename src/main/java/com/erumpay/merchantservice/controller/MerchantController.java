package com.erumpay.merchantservice.controller;

import com.erumpay.merchantservice.dto.MerchantCreateRequest;
import com.erumpay.merchantservice.dto.MerchantResponse;
import com.erumpay.merchantservice.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<MerchantResponse> getMerchants(Pageable pageable) {
        return merchantService.getMerchants(pageable);
    }
}