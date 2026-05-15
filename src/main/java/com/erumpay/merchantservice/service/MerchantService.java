package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.dto.MerchantCreateRequest;
import com.erumpay.merchantservice.dto.MerchantResponse;
import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Transactional
    public MerchantResponse createMerchant(MerchantCreateRequest request) {
        Merchant merchant = Merchant.builder()
                .name(request.name())
                .businessNumber(request.businessNumber())
                .ownerName(request.ownerName())
                .phoneNumber(request.phoneNumber())
                .build();

        return MerchantResponse.from(merchantRepository.save(merchant));
    }

    public MerchantResponse getMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found. id=" + merchantId));

        return MerchantResponse.from(merchant);
    }
}
