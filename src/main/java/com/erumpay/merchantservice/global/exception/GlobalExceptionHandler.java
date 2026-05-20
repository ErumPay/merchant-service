package com.erumpay.merchantservice.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMerchantException.class)
    public ResponseEntity<String> handleDuplicateMerchantException(
            DuplicateMerchantException e) {

        log.error("중복된 사업자번호 등록 요청 발생", e);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<String> handleMerchantNotFoundException(
            MerchantNotFoundException e) {

        log.error("가맹점 조회 실패", e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}