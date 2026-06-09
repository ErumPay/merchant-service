package com.erumpay.merchantservice.repository;

import com.erumpay.merchantservice.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    boolean existsByEventId(String eventId);
}
