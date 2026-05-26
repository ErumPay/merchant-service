package com.erumpay.merchantservice.entity;

import com.erumpay.merchantservice.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "pg_merchant_status_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantStatusHistory {
    @Id
    @GeneratedValue
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private MerchantStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private MerchantStatus toStatus;

    @Column(name = "reason", length = 200)
    private String reason;

    @Column(name = "changed_by", nullable = false)
    private Long changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MerchantStatusHistory(
            Merchant merchant,
            MerchantStatus fromStatus,
            MerchantStatus toStatus,
            String reason,
            Long changedBy
    ){
        this.merchant = merchant;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.changedBy = changedBy;
    }

    public static MerchantStatusHistory create(
            Merchant merchant,
            MerchantStatus fromStatus,
            MerchantStatus toStatus,
            String reason,
            Long changedBy
    ){
        LocalDateTime now = LocalDateTime.now();

        MerchantStatusHistory history = new MerchantStatusHistory();
        history.merchant = merchant;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.reason = reason;
        history.changedBy = changedBy;
        history.changedAt = now;
        history.createdAt = now;

        return  history;
    }
}
