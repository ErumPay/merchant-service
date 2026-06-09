package com.erumpay.merchantservice.service;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.Settlement;
import com.erumpay.merchantservice.global.exception.MerchantNotFoundException;
import com.erumpay.merchantservice.kafka.dto.PaymentSettlementCompletedEvent;
import com.erumpay.merchantservice.repository.MerchantRepository;
import com.erumpay.merchantservice.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSettlementService {

    private static final String PAYMENT_SETTLEMENT_COMPLETED = "PAYMENT_SETTLEMENT_COMPLETED";

    private final MerchantRepository merchantRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public void completeSettlement(PaymentSettlementCompletedEvent event) {
        validate(event);

        if (settlementRepository.existsByEventId(event.eventId())) {
            log.info("Settlement event already processed. eventId={}, paymentId={}",
                    event.eventId(), event.paymentId());
            return;
        }

        Merchant merchant = merchantRepository.findByMerchantIdAndDeletedAtIsNull(event.merchantId())
                .orElseThrow(() -> new MerchantNotFoundException("가맹점을 찾을 수 없습니다."));

        Settlement settlement = Settlement.completed(
                event.eventId(),
                merchant,
                event.paymentId(),
                event.amount(),
                event.occurredAt()
        );

        settlementRepository.save(settlement);

        log.info("Settlement completed. eventId={}, merchantId={}, paymentId={}, amount={}",
                event.eventId(), event.merchantId(), event.paymentId(), event.amount());
    }

    private void validate(PaymentSettlementCompletedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Payment settlement event is required.");
        }
        if (!PAYMENT_SETTLEMENT_COMPLETED.equals(event.eventType())) {
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
