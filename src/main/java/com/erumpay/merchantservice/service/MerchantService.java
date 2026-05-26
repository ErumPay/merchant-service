package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.dto.*;
import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.MerchantStatusHistory;
import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.global.exception.DuplicateMerchantException;
import com.erumpay.merchantservice.global.exception.MerchantNotFoundException;
import com.erumpay.merchantservice.repository.MerchantStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantStatusHistoryRepository merchantStatusHistoryRepository;

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Transactional
    public MerchantResponse createMerchant(MerchantCreateRequest request) {
        String apiKey = generateApiKey();

        Merchant merchant = Merchant.builder()
                .merchantName(request.merchantName())
                .businessNumber(request.businessNumber())
                .ownerName(request.ownerName())
                .contactPhone(request.contactPhone())
                .businessAddress(request.businessAddress())
                .categoryName(request.categoryName())
                .mccCode(request.mccCode())
                .feeRate(request.feeRate())
                .settlementAccount(request.settlementAccount())
                .apiKey(apiKey)
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
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return MerchantResponse.from(merchant);
    }

    public Page<MerchantResponse> getMerchants(Pageable pageable) {
        return merchantRepository.findByDeletedAtIsNull(pageable)
                .map(MerchantResponse::from);
    }

    @Transactional
    public MerchantResponse updateMerchant(Long merchantId, MerchantUpdateRequest request){
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
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
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        MerchantStatus fromStatus = merchant.getStatus();

        merchant.changeStatus(
                request.status(),
                request.suspendReason()
        );

        MerchantStatusHistory history = MerchantStatusHistory.create(
                merchant,
                fromStatus,
                request.status(),
                request.suspendReason(),
                0L
        );

        merchantStatusHistoryRepository.save(history);

        return MerchantResponse.from(merchant);
    }

    public InternalMerchantResponse getInternalMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return InternalMerchantResponse.from(merchant);
    }

    @Transactional
    public void deleteMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        merchant.softDelete();
    }

    public MerchantValidationResponse validateMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return MerchantValidationResponse.from(merchant);
    }

    public SettlementPolicyResponse getSettlementPolicy(Long merchantId) {
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return SettlementPolicyResponse.from(merchant);

    }

    public ReceiptMerchantInfoResponse getReceiptMerchantInfo(Long merchantId){
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return ReceiptMerchantInfoResponse.from(merchant);
    }

    @Transactional
    public ApiKeyRotateResponse rotateApiKey(Long merchantId) {
        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        String apiKey = generateApiKey();
        LocalDateTime rotatedAt = LocalDateTime.now();

        merchant.rotateApiKey(apiKey, rotatedAt);

        return ApiKeyRotateResponse.from(merchant);
    }

    public ApiKeyValidationResponse validateApiKey(ApiKeyValidationRequest request) {
        Optional<Merchant> merchantOptional = merchantRepository.findByApiKeyAndDeletedAtIsNull(request.apiKey());

        if (merchantOptional.isEmpty()) {
            return ApiKeyValidationResponse.invalid();
        }

        Merchant merchant = merchantOptional.get();

        if (merchant.getStatus() == MerchantStatus.ACTIVE
                && merchant.getApiKeyStatus() == ApiKeyStatus.ACTIVE) {
            return ApiKeyValidationResponse.valid(merchant);
        }

        return ApiKeyValidationResponse.invalid();
    }

    public List<MerchantStatusHistoryResponse> getMerchantStatusHistories(Long merchantId) {
        merchantRepository.findByMerchantIdAndDeletedAtIsNull(merchantId)
                .orElseThrow(() ->
                        new MerchantNotFoundException(
                                "Merchant not found. id=" + merchantId
                        )
                );

        return merchantStatusHistoryRepository.findByMerchantMerchantIdOrderByChangedAtDesc(merchantId)
                .stream()
                .map(MerchantStatusHistoryResponse::from)
                .toList();
    }
}