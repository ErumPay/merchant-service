package com.erumpay.merchantservice.kafka;

import com.erumpay.merchantservice.kafka.dto.PaymentSettlementCompletedEvent;
import com.erumpay.merchantservice.service.PaymentSettlementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSettlementEventConsumer {

    private static final String PAYMENT_SETTLEMENT_COMPLETED = "PAYMENT_SETTLEMENT_COMPLETED";

    private final ObjectMapper objectMapper;
    private final PaymentSettlementService paymentSettlementService;

    @KafkaListener(topics = "${merchant.kafka.topics.payment-settlement}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            PaymentSettlementCompletedEvent event = objectMapper.readValue(
                    record.value(),
                    PaymentSettlementCompletedEvent.class
            );
            if (!PAYMENT_SETTLEMENT_COMPLETED.equals(event.eventType())) {
                log.info("Skip unsupported payment settlement event. eventId={}, eventType={}, paymentId={}",
                        event.eventId(), event.eventType(), event.paymentId());
                return;
            }
            paymentSettlementService.completeSettlement(event);
        } catch (JsonProcessingException exception) {
            log.warn("Invalid payment settlement Kafka payload. topic={}, partition={}, offset={}, key={}",
                    record.topic(), record.partition(), record.offset(), record.key(), exception);
            throw new IllegalArgumentException("Invalid payment settlement Kafka payload.", exception);
        }
    }
}
