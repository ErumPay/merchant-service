package com.erumpay.merchantservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.erumpay.merchantservice.dto.MerchantCreateRequest;
import com.erumpay.merchantservice.dto.MerchantResponse;
import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.enums.MerchantStatus;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.repository.MerchantStatusHistoryRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private MerchantStatusHistoryRepository merchantStatusHistoryRepository;

    private MerchantService merchantService;

    @BeforeEach
    void setUp() {
        merchantService = new MerchantService(merchantRepository, merchantStatusHistoryRepository);
    }

    @Test
    void createMerchantStartsWithPendingStatus() {
        when(merchantRepository.existsByBusinessNumber("123-45-67890")).thenReturn(false);
        when(merchantRepository.save(any(Merchant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MerchantResponse response = merchantService.createMerchant(new MerchantCreateRequest(
                "테스트 가맹점",
                "123-45-67890",
                "홍길동",
                "010-1234-5678",
                "서울시 강남구",
                "음식점",
                "5812",
                new BigDecimal("2.50"),
                "110-123-456789"
        ));

        assertThat(response.status()).isEqualTo(MerchantStatus.PENDING);
    }
}
