package com.erumpay.merchantservice.repository;

import com.erumpay.merchantservice.entity.Merchant;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByBusinessNumber(String businessNumber);

    boolean existsByBusinessNumber(String businessNumber);

    Optional<Merchant> findByMerchantIdAndDeletedAtIsNull(Long merchantId);

    Page<Merchant> findByDeletedAtIsNull(Pageable pageable);
}
