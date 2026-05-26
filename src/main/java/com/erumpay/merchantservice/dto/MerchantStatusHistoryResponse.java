package com.erumpay.merchantservice.dto;

import com.erumpay.merchantservice.entity.Merchant;
import com.erumpay.merchantservice.entity.MerchantStatusHistory;
import com.erumpay.merchantservice.enums.MerchantStatus;

import java.time.LocalDateTime;

public record MerchantStatusHistoryResponse (
        Long historyId,
        Long merchantId,
        MerchantStatus fromStatus,
        MerchantStatus toStatus,
        String reason,
        Long changedBy,
        LocalDateTime changedAt,
        LocalDateTime createdAt
) {
    public static MerchantStatusHistoryResponse from(MerchantStatusHistory history){
        return new MerchantStatusHistoryResponse (
            history.getHistoryId(),
            history.getMerchant().getMerchantId(),
            history.getFromStatus(),
            history.getToStatus(),
            history.getReason(),
            history.getChangedBy(),
            history.getChangedAt(),
            history.getCreatedAt()
        );
    }
}
