package com.erumpay.merchantservice.repository;

import com.erumpay.merchantservice.entity.MerchantStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantStatusHistoryRepository extends JpaRepository<MerchantStatusHistory, Long> {
    List<MerchantStatusHistory> findByMerchantMerchantIdOrderByChangedAtDesc(Long merchantId);
}
