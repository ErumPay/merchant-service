package com.erumpay.merchantservice.controller;

import com.erumpay.merchantservice.dto.MerchantResponse;
import com.erumpay.merchantservice.service.MerchantService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/merchants")
public class InternalMerchantController {

    private final MerchantService merchantService;

    @GetMapping("/{merchantId}")
    public MerchantResponse InternalMerchantController(@PathVariable Long merchantId) {
        return merchantService.getMerchant(merchantId);
    }
}
