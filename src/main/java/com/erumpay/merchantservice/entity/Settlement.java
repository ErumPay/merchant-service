package com.erumpay.merchantservice.entity;

import com.erumpay.merchantservice.enums.SettlementStatus;
import com.erumpay.merchantservice.enums.SettlementPeriodType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Entity
@Table(name = "pg_settlements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long settlementId;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 20)
    private SettlementPeriodType periodType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "total_sales", nullable = false)
    private Long totalSales;

    @Column(name = "cancel_amount", nullable = false)
    private Long cancelAmount;

    @Column(name = "net_sales", nullable = false)
    private Long netSales;

    @Column(name = "settlement_amount", nullable = false)
    private Long settlementAmount;

    @Column(name = "fee_amount", nullable = false)
    private Long feeAmount;

    @Column(name = "fee_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal feeRate;

    @Column(name = "payment_count", nullable = false)
    private Long paymentCount;

    @Column(name = "cancel_count", nullable = false)
    private Long cancelCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SettlementStatus status;

    @Column(name = "settled_at", nullable = false)
    private LocalDateTime settledAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Settlement completed(
            String eventId,
            Merchant merchant,
            Long paymentId,
            Long amount,
            LocalDateTime settledAt,
            Long totalSales,
            Long cancelAmount,
            Long paymentCount,
            Long cancelCount
    ) {
        return create(
                eventId,
                merchant,
                paymentId,
                amount,
                settledAt,
                totalSales,
                cancelAmount,
                paymentCount,
                cancelCount,
                SettlementStatus.COMPLETED
        );
    }

    public static Settlement canceled(
            String eventId,
            Merchant merchant,
            Long paymentId,
            Long amount,
            LocalDateTime settledAt,
            Long totalSales,
            Long cancelAmount,
            Long paymentCount,
            Long cancelCount
    ) {
        return create(
                eventId,
                merchant,
                paymentId,
                amount,
                settledAt,
                totalSales,
                cancelAmount,
                paymentCount,
                cancelCount,
                SettlementStatus.CANCELED
        );
    }

    private static Settlement create(
            String eventId,
            Merchant merchant,
            Long paymentId,
            Long amount,
            LocalDateTime settledAt,
            Long totalSales,
            Long cancelAmount,
            Long paymentCount,
            Long cancelCount,
            SettlementStatus status
    ) {
        Settlement settlement = new Settlement();
        Long netSales = totalSales - cancelAmount;
        Long feeAmount = calculateFeeAmount(netSales, merchant.getFeeRate());

        settlement.eventId = eventId;
        settlement.merchant = merchant;
        settlement.paymentId = paymentId;
        settlement.amount = amount;
        settlement.periodType = SettlementPeriodType.DAILY;
        settlement.periodStart = settledAt.toLocalDate();
        settlement.periodEnd = settledAt.toLocalDate();
        settlement.totalSales = totalSales;
        settlement.cancelAmount = cancelAmount;
        settlement.netSales = netSales;
        settlement.feeRate = merchant.getFeeRate();
        settlement.feeAmount = feeAmount;
        settlement.settlementAmount = netSales - feeAmount;
        settlement.paymentCount = paymentCount;
        settlement.cancelCount = cancelCount;
        settlement.status = status;
        settlement.settledAt = settledAt;
        settlement.createdAt = LocalDateTime.now();
        settlement.updatedAt = settlement.createdAt;
        return settlement;
    }

    private static Long calculateFeeAmount(Long amount, BigDecimal feeRate) {
        return BigDecimal.valueOf(amount)
                .multiply(feeRate)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValue();
    }
}
