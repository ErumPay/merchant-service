package com.erumpay.merchantservice.global.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.SocketTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("요청 검증 실패는 MER-REQ-001로 응답한다")
    void handleInvalidRequest() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc",
                Long.class,
                "merchantId",
                null,
                new NumberFormatException()
        );

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception, request());

        assertErrorResponse(response, ErrorCode.INVALID_REQUEST);
        assertThat(response.getBody().details())
                .containsExactly(new FieldErrorDetail("merchantId", "요청 값의 형식이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("가맹점 없음은 MER-MCH-201로 응답한다")
    void handleMerchantNotFound() {
        MerchantNotFoundException exception = new MerchantNotFoundException("가맹점을 찾을 수 없습니다.");

        ResponseEntity<ErrorResponse> response = handler.handleMerchantException(exception, request());

        assertErrorResponse(response, ErrorCode.MERCHANT_NOT_FOUND);
    }

    @Test
    @DisplayName("가맹점 상태 충돌은 MER-MCH-203으로 응답한다")
    void handleInvalidMerchantStatus() {
        InvalidMerchantStatusException exception = new InvalidMerchantStatusException(
                "정지 상태로 변경할 때는 정지 사유가 필요합니다."
        );

        ResponseEntity<ErrorResponse> response = handler.handleMerchantException(exception, request());

        assertErrorResponse(response, ErrorCode.INVALID_MERCHANT_STATUS, exception.getMessage());
    }

    @Test
    @DisplayName("이미 삭제된 가맹점은 MER-MCH-202로 응답한다")
    void handleMerchantAlreadyDeleted() {
        MerchantAlreadyDeletedException exception = new MerchantAlreadyDeletedException("이미 삭제된 가맹점입니다.");

        ResponseEntity<ErrorResponse> response = handler.handleMerchantException(exception, request());

        assertErrorResponse(response, ErrorCode.MERCHANT_ALREADY_DELETED);
    }

    @Test
    @DisplayName("중복 가맹점은 MER-MCH-301로 응답한다")
    void handleDuplicateMerchant() {
        DuplicateMerchantException exception = new DuplicateMerchantException("이미 등록된 사업자번호입니다.");

        ResponseEntity<ErrorResponse> response = handler.handleMerchantException(exception, request());

        assertErrorResponse(response, ErrorCode.DUPLICATE_MERCHANT);
    }

    @Test
    @DisplayName("멱등키 누락은 MER-IDM-001로 응답한다")
    void handleIdempotencyKeyRequired() {
        IdempotencyKeyRequiredException exception = new IdempotencyKeyRequiredException("멱등키가 필요합니다.");

        ResponseEntity<ErrorResponse> response = handler.handleMerchantException(exception, request());

        assertErrorResponse(response, ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
    }

    @Test
    @DisplayName("외부 서비스 사용 불가는 MER-EXT-400으로 응답한다")
    void handleExternalServiceUnavailable() {
        ResourceAccessException exception = new ResourceAccessException("connection refused");

        ResponseEntity<ErrorResponse> response = handler.handleExternalResourceAccessException(exception, request());

        assertErrorResponse(response, ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE);
    }

    @Test
    @DisplayName("외부 서비스 타임아웃은 MER-EXT-402로 응답한다")
    void handleExternalServiceTimeout() {
        ResourceAccessException exception = new ResourceAccessException(
                "external service timeout",
                new SocketTimeoutException("read timed out")
        );

        ResponseEntity<ErrorResponse> response = handler.handleExternalResourceAccessException(exception, request());

        assertErrorResponse(response, ErrorCode.EXTERNAL_SERVICE_TIMEOUT);
    }

    @Test
    @DisplayName("RestClientException은 MER-EXT-400으로 응답한다")
    void handleRestClientException() {
        RestClientException exception = new RestClientException("external service unavailable");

        ResponseEntity<ErrorResponse> response = handler.handleExternalServiceException(exception, request());

        assertErrorResponse(response, ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE);
    }

    @Test
    @DisplayName("DB 저장 실패는 MER-DB-901로 응답한다")
    void handleMerchantSaveFailed() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("save failed");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(exception, request());

        assertErrorResponse(response, ErrorCode.MERCHANT_SAVE_FAILED);
    }

    @Test
    @DisplayName("DB 접근 실패는 MER-DB-901로 응답한다")
    void handleDatabaseAccessFailed() {
        DataAccessResourceFailureException exception = new DataAccessResourceFailureException("database unavailable");

        ResponseEntity<ErrorResponse> response = handler.handleDataAccessException(exception, request());

        assertErrorResponse(response, ErrorCode.MERCHANT_SAVE_FAILED);
    }

    @Test
    @DisplayName("알 수 없는 내부 오류는 MER-SYS-900으로 응답한다")
    void handleInternalServerError() {
        Exception exception = new RuntimeException("unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleException(exception, request());

        assertErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test");
        request.addHeader("X-Request-Id", "req-test");
        return request;
    }

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response, ErrorCode errorCode) {
        assertErrorResponse(response, errorCode, errorCode.getMessage());
    }

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response, ErrorCode errorCode, String message) {
        assertThat(response.getStatusCode()).isEqualTo(errorCode.getHttpStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(response.getBody().error()).isEqualTo(errorCode.getHttpStatus().name());
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().reason()).isEqualTo(errorCode.getReason());
        assertThat(response.getBody().message()).isEqualTo(message);
        assertThat(response.getBody().requestId()).isEqualTo("req-test");
    }
}
