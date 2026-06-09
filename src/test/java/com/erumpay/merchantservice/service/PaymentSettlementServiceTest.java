package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.Settlement;
import com.erumpay.merchantservice.enums.SettlementPeriodType;
import com.erumpay.merchantservice.enums.SettlementStatus;
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
        mockSettlementSummary(7L, occurredAt, 0L, 0L, 0L, 0L);

        paymentSettlementService.completeSettlement(event);

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        Settlement settlement = settlementCaptor.getValue();
        assertThat(settlement.getEventId()).isEqualTo(event.eventId());
        assertThat(settlement.getPaymentId()).isEqualTo(101L);
        assertThat(settlement.getAmount()).isEqualTo(25000L);
        assertThat(settlement.getTotalSales()).isEqualTo(25000L);
        assertThat(settlement.getCancelAmount()).isZero();
        assertThat(settlement.getNetSales()).isEqualTo(25000L);
        assertThat(settlement.getSettledAt()).isEqualTo(occurredAt);
    }

    @Test
    void completeSettlementCreatesNewSettlementWithMerchantDailySummary() {
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
        mockSettlementSummary(7L, occurredAt, 25000L, 5000L, 2L, 1L);

        paymentSettlementService.completeSettlement(event);

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        Settlement settlement = settlementCaptor.getValue();
        assertThat(settlement.getEventId()).isEqualTo(event.eventId());
        assertThat(settlement.getPaymentId()).isEqualTo(102L);
        assertThat(settlement.getAmount()).isEqualTo(10000L);
        assertThat(settlement.getTotalSales()).isEqualTo(35000L);
        assertThat(settlement.getCancelAmount()).isEqualTo(5000L);
        assertThat(settlement.getNetSales()).isEqualTo(30000L);
        assertThat(settlement.getPaymentCount()).isEqualTo(3L);
        assertThat(settlement.getCancelCount()).isEqualTo(1L);
        assertThat(settlement.getFeeAmount()).isEqualTo(450L);
        assertThat(settlement.getSettlementAmount()).isEqualTo(29550L);
    }

    @Test
    void cancelSettlementCreatesNewSettlementWithMerchantDailySummary() {
        LocalDateTime occurredAt = LocalDateTime.of(2026, 6, 8, 19, 0);
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
                "payment:settlement:canceled:101:7",
                "PAYMENT_SETTLEMENT_CANCELED",
                7L,
                101L,
                10000L,
                occurredAt
        );

        when(settlementRepository.existsByEventId(event.eventId())).thenReturn(false);
        when(merchantRepository.findByMerchantIdAndDeletedAtIsNull(7L)).thenReturn(Optional.of(merchant));
        mockSettlementSummary(7L, occurredAt, 35000L, 5000L, 3L, 1L);

        paymentSettlementService.handleSettlement(event);

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(settlementRepository).save(settlementCaptor.capture());
        Settlement settlement = settlementCaptor.getValue();
        assertThat(settlement.getEventId()).isEqualTo(event.eventId());
        assertThat(settlement.getPaymentId()).isEqualTo(101L);
        assertThat(settlement.getAmount()).isEqualTo(10000L);
        assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.CANCELED);
        assertThat(settlement.getTotalSales()).isEqualTo(35000L);
        assertThat(settlement.getCancelAmount()).isEqualTo(15000L);
        assertThat(settlement.getNetSales()).isEqualTo(20000L);
        assertThat(settlement.getPaymentCount()).isEqualTo(3L);
        assertThat(settlement.getCancelCount()).isEqualTo(2L);
        assertThat(settlement.getFeeAmount()).isEqualTo(300L);
        assertThat(settlement.getSettlementAmount()).isEqualTo(19700L);
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

    private void mockSettlementSummary(
            Long merchantId,
            LocalDateTime occurredAt,
            Long completedAmount,
            Long canceledAmount,
            Long completedCount,
            Long canceledCount
    ) {
        when(settlementRepository.sumAmountByMerchantAndPeriodAndStatus(
                merchantId,
                SettlementPeriodType.DAILY,
                occurredAt.toLocalDate(),
                SettlementStatus.COMPLETED
        )).thenReturn(completedAmount);
        when(settlementRepository.sumAmountByMerchantAndPeriodAndStatus(
                merchantId,
                SettlementPeriodType.DAILY,
                occurredAt.toLocalDate(),
                SettlementStatus.CANCELED
        )).thenReturn(canceledAmount);
        when(settlementRepository.countByMerchantMerchantIdAndPeriodTypeAndPeriodStartAndStatus(
                merchantId,
                SettlementPeriodType.DAILY,
                occurredAt.toLocalDate(),
                SettlementStatus.COMPLETED
        )).thenReturn(completedCount);
        when(settlementRepository.countByMerchantMerchantIdAndPeriodTypeAndPeriodStartAndStatus(
                merchantId,
                SettlementPeriodType.DAILY,
                occurredAt.toLocalDate(),
                SettlementStatus.CANCELED
        )).thenReturn(canceledCount);
    }
}
