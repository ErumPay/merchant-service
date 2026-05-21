package com.erumpay.merchantservice.entity;

import com.erumpay.merchantservice.enums.ApiKeyStatus;
import com.erumpay.merchantservice.enums.MerchantStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "pg_merchants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "merchant_name", nullable = false, length = 100)
    private String merchantName;

    @Column(name = "business_number", unique = true, nullable = false, length = 20)
    private String businessNumber;

    @Column(name = "owner_name", nullable = false, length = 50)
    private String ownerName;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "business_address", nullable = false, length = 255)
    private String businessAddress;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(name = "mcc_code", nullable = false, length = 4)
    private String mccCode;

    @Column(name = "api_key", unique = true, nullable = false, length = 255)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_key_status", nullable = false)
    private ApiKeyStatus apiKeyStatus;

    @Column(name = "api_key_issued_at", nullable = false)
    private LocalDateTime apiKeyIssuedAt;

    @Column(name = "api_key_rotated_at")
    private LocalDateTime apiKeyRotatedAt;

    @Column(name = "fee_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal feeRate;

    @Column(name = "settlement_account", length = 100, nullable = false)
    private String settlementAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MerchantStatus status;

    @Column(name = "suspend_reason", length = 200)
    private String suspendReason;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Merchant(String merchantName,
                    String businessNumber,
                    String ownerName,
                    String contactPhone,
                    String businessAddress,
                    String categoryName,
                    String mccCode,
                    String apiKey,
                    ApiKeyStatus apiKeyStatus,
                    LocalDateTime apiKeyIssuedAt,
                    BigDecimal feeRate,
                    String settlementAccount,
                    MerchantStatus status) {
        this.merchantName = merchantName;
        this.businessNumber = businessNumber;
        this.ownerName = ownerName;
        this.contactPhone = contactPhone;
        this.businessAddress = businessAddress;
        this.categoryName = categoryName;
        this.mccCode = mccCode;
        this.apiKey = apiKey;
        this.apiKeyStatus = apiKeyStatus;
        this.apiKeyIssuedAt = apiKeyIssuedAt;
        this.feeRate = feeRate;
        this.settlementAccount = settlementAccount;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInfo(
            String merchantName,
            String ownerName,
            String contactPhone,
            String businessAddress,
            String categoryName,
            String mccCode,
            BigDecimal feeRate,
            String settlementAccount
    ){
        this.merchantName = merchantName;
        this.ownerName = ownerName;
        this.contactPhone = contactPhone;
        this.businessAddress = businessAddress;
        this.categoryName = categoryName;
        this.mccCode = mccCode;
        this.feeRate = feeRate;
        this.settlementAccount = settlementAccount;
    }



}
