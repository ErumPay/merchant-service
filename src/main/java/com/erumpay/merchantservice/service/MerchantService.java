package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.dto.*;
import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.global.exception.DuplicateMerchantException;
import com.erumpay.merchantservice.global.exception.MerchantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

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

        try {
            return MerchantResponse.from(merchantRepository.save(merchant));
        } catch (DataIntegrityViolationException e) {

            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("business_number")) {
                throw new DuplicateMerchantException(
                        "이미 등록된 사업자번호입니다.",
                        e
                );
            }

            throw e;
        }
    }

    public MerchantResponse getMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return MerchantResponse.from(merchant);
    }

    public Page<MerchantResponse> getMerchants(Pageable pageable) {
        return merchantRepository.findAll(pageable)
                .map(MerchantResponse::from);
    }

    @Transactional
    public MerchantResponse updateMerchant(Long merchantId, MerchantUpdateRequest request){
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

       merchant.updateInfo(
                request.merchantName(),
                request.ownerName(),
                request.contactPhone(),
                request.businessAddress(),
                request.categoryName(),
                request.mccCode(),
                request.feeRate(),
                request.settlementAccount()
        );

        return MerchantResponse.from(merchant);

    }

    @Transactional
    public MerchantResponse updateMerchantStatus(Long merchantId, MerchantStatusUpdateRequest request){
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        merchant.changeStatus(
                request.status(),
                request.suspendReason()
        );

        return MerchantResponse.from(merchant);
    }

    public InternalMerchantResponse getInternalMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return InternalMerchantResponse.from(merchant);
    }

    @Transactional
    public void deleteMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        merchant.softDelete();
    }

    public MerchantValidationResponse validateMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return MerchantValidationResponse.from(merchant);
    }

    public SettlementPolicyResponse getSettlementPolicy(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return SettlementPolicyResponse.from(merchant);

    }
}