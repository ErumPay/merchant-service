package com.erumpay.merchantservice.global.exception;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMerchantException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateMerchantException(
            DuplicateMerchantException e) {

        log.warn("중복된 사업자번호 등록 요청 발생", e);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT.name(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        MDC.get("traceId")
                ));
    }

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMerchantNotFoundException(
            MerchantNotFoundException e) {

        log.debug("가맹점 조회 실패", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.name(),
                        e.getMessage(),
                        LocalDateTime.now(),
                        MDC.get("traceId")
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("예상하지 못한 서버 오류 발생", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.name(),
                        "서버 내부 오류가 발생했습니다.",
                        LocalDateTime.now(),
                        MDC.get("traceId")
                ));
    }
}