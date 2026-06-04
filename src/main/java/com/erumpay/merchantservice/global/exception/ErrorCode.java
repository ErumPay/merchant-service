package com.erumpay.merchantservice.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "MER-REQ-001",
            "MERCHANT_INVALID_REQUEST",
            "잘못된 가맹점 요청입니다."
    ),
    MERCHANT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MER-MCH-201",
            "MERCHANT_NOT_FOUND",
            "가맹점을 찾을 수 없습니다."
    ),
    MERCHANT_ALREADY_DELETED(
            HttpStatus.CONFLICT,
            "MER-MCH-202",
            "MERCHANT_ALREADY_DELETED",
            "이미 삭제된 가맹점입니다."
    ),
    INVALID_MERCHANT_STATUS(
            HttpStatus.CONFLICT,
            "MER-MCH-203",
            "INVALID_MERCHANT_STATUS",
            "현재 가맹점 상태에서는 요청한 작업을 처리할 수 없습니다."
    ),
    DUPLICATE_MERCHANT(
            HttpStatus.CONFLICT,
            "MER-MCH-301",
            "DUPLICATE_MERCHANT",
            "이미 등록된 사업자번호입니다."
    ),
    IDEMPOTENCY_KEY_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "MER-IDM-001",
            "IDEMPOTENCY_KEY_REQUIRED",
            "멱등키가 필요합니다."
    ),
    EXTERNAL_SERVICE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "MER-EXT-400",
            "EXTERNAL_SERVICE_UNAVAILABLE",
            "외부 서비스를 일시적으로 사용할 수 없습니다."
    ),
    EXTERNAL_SERVICE_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "MER-EXT-402",
            "EXTERNAL_SERVICE_TIMEOUT",
            "외부 서비스 응답 시간이 초과되었습니다."
    ),
    MERCHANT_SAVE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MER-DB-901",
            "MERCHANT_SAVE_FAILED",
            "가맹점 정보 저장에 실패했습니다."
    ),
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "MER-SYS-900",
            "INTERNAL_SERVER_ERROR",
            "알 수 없는 내부 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;
    private final String message;
}
