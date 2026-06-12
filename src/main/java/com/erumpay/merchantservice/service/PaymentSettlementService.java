package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.Settlement;
import com.erumpay.merchantservice.enums.SettlementPeriodType;
import com.erumpay.merchantservice.enums.SettlementStatus;
import com.erumpay.merchantservice.global.exception.MerchantNotFoundException;
import com.erumpay.merchantservice.kafka.dto.PaymentSettlementCompletedEvent;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSettlementService {

    private static final String PAYMENT_SETTLEMENT_COMPLETED = "PAYMENT_SETTLEMENT_COMPLETED";
    private static final String PAYMENT_SETTLEMENT_CANCELED = "PAYMENT_SETTLEMENT_CANCELED";

    private final MerchantRepository merchantRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public void handleSettlement(PaymentSettlementCompletedEvent event) {
        validate(event);

        if (settlementRepository.existsByEventId(event.eventId())) {
            log.info("Settlement event already processed. eventId={}, paymentId={}",
                    event.eventId(), event.paymentId());
            return;
        }

        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(event.merchantId())
                .orElseThrow(() -> new MerchantNotFoundException("가맹점을 찾을 수 없습니다."));

        LocalDate settlementDate = event.occurredAt().toLocalDate();
        Long completedAmount = settlementRepository.sumAmountByMerchantAndPeriodAndStatus(
                event.merchantId(),
                SettlementPeriodType.DAILY,
                settlementDate,
                SettlementStatus.COMPLETED
        );
        Long canceledAmount = settlementRepository.sumAmountByMerchantAndPeriodAndStatus(
                event.merchantId(),
                SettlementPeriodType.DAILY,
                settlementDate,
                SettlementStatus.CANCELED
        );
        Long completedCount = settlementRepository.countByMerchantMerchantIdAndPeriodTypeAndPeriodStartAndStatus(
                event.merchantId(),
                SettlementPeriodType.DAILY,
                settlementDate,
                SettlementStatus.COMPLETED
        );
        Long canceledCount = settlementRepository.countByMerchantMerchantIdAndPeriodTypeAndPeriodStartAndStatus(
                event.merchantId(),
                SettlementPeriodType.DAILY,
                settlementDate,
                SettlementStatus.CANCELED
        );

        Settlement settlement = switch (event.eventType()) {
            case PAYMENT_SETTLEMENT_COMPLETED -> Settlement.completed(
                    event.eventId(),
                    merchant,
                    event.paymentId(),
                    event.amount(),
                    event.occurredAt(),
                    completedAmount + event.amount(),
                    canceledAmount,
                    completedCount + 1,
                    canceledCount
            );
            case PAYMENT_SETTLEMENT_CANCELED -> Settlement.canceled(
                    event.eventId(),
                    merchant,
                    event.paymentId(),
                    event.amount(),
                    event.occurredAt(),
                    completedAmount,
                    canceledAmount + event.amount(),
                    completedCount,
                    canceledCount + 1
            );
            default -> throw new IllegalArgumentException("Unsupported payment settlement eventType: " + event.eventType());
        };

        settlementRepository.save(settlement);

        log.info("Settlement event processed. eventId={}, eventType={}, merchantId={}, paymentId={}, amount={}",
                event.eventId(), event.eventType(), event.merchantId(), event.paymentId(), event.amount());
    }

    @Transactional
    public void completeSettlement(PaymentSettlementCompletedEvent event) {
        handleSettlement(event);
    }

    private void validate(PaymentSettlementCompletedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Payment settlement event is required.");
        }
        if (!PAYMENT_SETTLEMENT_COMPLETED.equals(event.eventType())
                && !PAYMENT_SETTLEMENT_CANCELED.equals(event.eventType())) {
            throw new IllegalArgumentException("Unsupported payment settlement eventType: " + event.eventType());
        }
        if (!StringUtils.hasText(event.eventId())) {
            throw new IllegalArgumentException("Payment settlement eventId is required.");
        }
        if (event.merchantId() == null) {
            throw new IllegalArgumentException("Payment settlement merchantId is required.");
        }
        if (event.paymentId() == null) {
            throw new IllegalArgumentException("Payment settlement paymentId is required.");
        }
        if (event.amount() == null) {
            throw new IllegalArgumentException("Payment settlement amount is required.");
        }
        if (event.occurredAt() == null) {
            throw new IllegalArgumentException("Payment settlement occurredAt is required.");
        }
    }
}
