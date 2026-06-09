package com.erumpay.merchantservice.repository;

import com.erumpay.merchantservice.entity.Settlement;
import com.erumpay.merchantservice.enums.SettlementPeriodType;
import com.erumpay.merchantservice.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    boolean existsByEventId(String eventId);

    @Query("""
            select coalesce(sum(s.amount), 0)
            from Settlement s
            where s.merchant.merchantId = :merchantId
              and s.periodType = :periodType
              and s.periodStart = :periodStart
              and s.status = :status
            """)
    Long sumAmountByMerchantAndPeriodAndStatus(
            Long merchantId,
            SettlementPeriodType periodType,
            LocalDate periodStart,
            SettlementStatus status
    );

    Long countByMerchantMerchantIdAndPeriodTypeAndPeriodStartAndStatus(
            Long merchantId,
            SettlementPeriodType periodType,
            LocalDate periodStart,
            SettlementStatus status
    );
}
