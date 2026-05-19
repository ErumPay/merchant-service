package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.dto.MerchantCreateRequest;
import com.erumpay.merchantservice.dto.MerchantResponse;
import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;
import com.erumpay.merchantservice.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Transactional
    public MerchantResponse createMerchant(MerchantCreateRequest request) {
        Merchant merchant = Merchant.builder()
                .merchantName(request.merchantName())
                .businessNumber(request.businessNumber())
                .ownerName(request.ownerName())
                .contactPhone(request.contactPhone())
                .businessAddress(request.businessAddress())
                .categoryName(request.categoryName())
                .mccCode(request.mccCode())
                .apiKey(request.apiKey())
                .feeRate(request.feeRate())
                .settlementAccount(request.settlementAccount())
                .apiKeyStatus(ApiKeyStatus.ACTIVE)
                .apiKeyIssuedAt(LocalDateTime.now())
                .status(MerchantStatus.DRAFT)
                .build();

        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    public MerchantResponse getMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found. id=" + merchantId));

        return MerchantResponse.from(merchant);
    }
}
