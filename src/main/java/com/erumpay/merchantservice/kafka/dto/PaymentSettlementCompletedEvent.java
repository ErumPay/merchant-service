package com.erumpay.merchantservice.kafka.dto;

import java.time.LocalDateTime;

public record PaymentSettlementCompletedEvent(
        String eventId,
        String eventType,
        Long merchantId,
        Long paymentId,
        Long amount,
        LocalDateTime occurredAt
) {
}
