package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.Settlement;
import com.erumpay.merchantservice.kafka.dto.PaymentSettlementCompletedEvent;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentSettlementServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private SettlementRepository settlementRepository;

    @InjectMocks
    private PaymentSettlementService paymentSettlementService;

    @Test
    void completeSettlementSavesPaymentSettlement() {
        LocalDateTime occurredAt = LocalDateTime.of(2026, 6, 8, 17, 0);
        PaymentSettlementCompletedEvent event = new PaymentSettlementCompletedEvent(
                "payment:settlement:completed:101:7",
                "PAYMENT_SETTLEMENT_COMPLETED",
                7L,
                101L,
                25000L,
                occurredAt
        );
        Merchant merchant = Merchant.builder()
                .merchantName("테스트 가맹점")
                .businessNumber("123-45-67890")
                .ownerName("홍길동")
                .contactPhone("010-0000-0000")
                .businessAddress("서울")
                .categoryName("음식점")
                .mccCode("5812")
                .apiKey("api-key")
                .apiKeyStatus(com.erumpay.merchantservice.enums.ApiKeyStatus.ACTIVE)
                .apiKeyIssuedAt(occurredAt)
                .feeRate(java.math.BigDecimal.valueOf(1.5))
                .settlementAccount("은행 123")
                .status(com.erumpay.merchantservice.enums.MerchantStatus.ACTIVE)
                .build();

        when(settlementRepository.existsByEventId(event.eventId())).thenReturn(false);
        when(merchantRepository.findByMerchantIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(merchant));

        paymentSettlementService.completeSettlement(event);

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        Settlement settlement = settlementCaptor.getValue();
        assertThat(settlement.getEventId()).isEqualTo(event.eventId());
        assertThat(settlement.getPaymentId()).isEqualTo(101L);
        assertThat(settlement.getAmount()).isEqualTo(25000L);
        assertThat(settlement.getSettledAt()).isEqualTo(occurredAt);
    }

    @Test
    void completeSettlementCreatesNewSettlementPerPaymentEvent() {
        LocalDateTime occurredAt = LocalDateTime.of(2026, 6, 8, 18, 0);
        Merchant merchant = Merchant.builder()
                .merchantName("테스트 가맹점")
                .businessNumber("123-45-67890")
                .ownerName("홍길동")
                .contactPhone("010-0000-0000")
                .businessAddress("서울")
                .categoryName("음식점")
                .mccCode("5812")
                .apiKey("api-key")
                .apiKeyStatus(com.erumpay.merchantservice.enums.ApiKeyStatus.ACTIVE)
                .apiKeyIssuedAt(occurredAt)
                .feeRate(java.math.BigDecimal.valueOf(1.5))
                .settlementAccount("은행 123")
                .status(com.erumpay.merchantservice.enums.MerchantStatus.ACTIVE)
                .build();
        PaymentSettlementCompletedEvent event = new PaymentSettlementCompletedEvent(
                "payment:settlement:completed:102:7",
                "PAYMENT_SETTLEMENT_COMPLETED",
                7L,
                102L,
                10000L,
                occurredAt
        );

        when(settlementRepository.existsByEventId(event.eventId())).thenReturn(false);
        when(merchantRepository.findByMerchantIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(merchant));

        paymentSettlementService.completeSettlement(event);

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        Settlement settlement = settlementCaptor.getValue();
        assertThat(settlement.getEventId()).isEqualTo(event.eventId());
        assertThat(settlement.getPaymentId()).isEqualTo(102L);
        assertThat(settlement.getAmount()).isEqualTo(10000L);
        assertThat(settlement.getTotalSales()).isEqualTo(10000L);
        assertThat(settlement.getPaymentCount()).isEqualTo(1L);
        assertThat(settlement.getFeeAmount()).isEqualTo(150L);
        assertThat(settlement.getSettlementAmount()).isEqualTo(9850L);
    }

    @Test
    void completeSettlementSkipsAlreadyProcessedEvent() {
        PaymentSettlementCompletedEvent event = new PaymentSettlementCompletedEvent(
                "payment:settlement:completed:101:7",
                "PAYMENT_SETTLEMENT_COMPLETED",
                7L,
                101L,
                25000L,
                LocalDateTime.of(2026, 6, 8, 17, 0)
        );

        when(settlementRepository.existsByEventId(event.eventId())).thenReturn(true);

        paymentSettlementService.completeSettlement(event);

        verify(merchantRepository, never()).findByMerchantIdAndDeletedAtIsNull(any());
        verify(settlementRepository, never()).save(any());
    }
}
