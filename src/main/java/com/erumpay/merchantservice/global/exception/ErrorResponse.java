package com.erumpay.merchantservice.global.exception;

import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String code,
        String reason,
        String message,
        List<FieldErrorDetail> details,
        String requestId
) {

    public static ErrorResponse of(ErrorCode errorCode, String message, List<FieldErrorDetail> details, String requestId) {
        return new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus().name(),
                errorCode.getCode(),
                errorCode.getReason(),
                message == null ? errorCode.getMessage() : message,
                details,
                requestId
        );
    }
}
