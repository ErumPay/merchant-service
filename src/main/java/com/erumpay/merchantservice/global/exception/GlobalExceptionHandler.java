package com.erumpay.merchantservice.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MerchantException.class)
    public ResponseEntity<ErrorResponse> handleMerchantException(
            MerchantException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("Merchant service exception. code={}, reason={}, path={}",
                errorCode.getCode(),
                errorCode.getReason(),
                request.getRequestURI(),
                exception
        );

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, exception.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(
            Exception exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        List<FieldErrorDetail> details = extractDetails(exception);

        log.warn("Invalid merchant request. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), details, requestId(request)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        log.warn("Invalid merchant argument. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, exception.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleExternalResourceAccessException(
            ResourceAccessException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = isTimeout(exception)
                ? ErrorCode.EXTERNAL_SERVICE_TIMEOUT
                : ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE;
        log.error("External service resource access failed. code={}, path={}",
                errorCode.getCode(),
                request.getRequestURI(),
                exception
        );

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            RestClientException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE;
        log.error("External service communication failed. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.MERCHANT_SAVE_FAILED;
        log.error("Merchant data save failed. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.MERCHANT_SAVE_FAILED;
        log.error("Merchant database access failed. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), List.of(), requestId(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unexpected merchant service error. path={}", request.getRequestURI(), exception);

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage(), List.of(), requestId(request)));
    }

    private List<FieldErrorDetail> extractDetails(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            return methodArgumentNotValidException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                    .toList();
        }

        if (exception instanceof BindException bindException) {
            return bindException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                    .toList();
        }

        if (exception instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(violation -> new FieldErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ))
                    .toList();
        }

        if (exception instanceof HandlerMethodValidationException handlerMethodValidationException) {
            return extractHandlerMethodValidationDetails(handlerMethodValidationException);
        }

        if (exception instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            return List.of(new FieldErrorDetail(
                    methodArgumentTypeMismatchException.getName(),
                    "요청 값의 형식이 올바르지 않습니다."
            ));
        }

        if (exception instanceof MissingServletRequestParameterException missingParameterException) {
            return List.of(new FieldErrorDetail(
                    missingParameterException.getParameterName(),
                    "필수 요청 파라미터입니다."
            ));
        }

        if (exception instanceof MissingRequestHeaderException missingHeaderException) {
            return List.of(new FieldErrorDetail(
                    missingHeaderException.getHeaderName(),
                    "필수 요청 헤더입니다."
            ));
        }

        if (exception instanceof MissingPathVariableException missingPathVariableException) {
            return List.of(new FieldErrorDetail(
                    missingPathVariableException.getVariableName(),
                    "필수 경로 변수입니다."
            ));
        }

        return List.of();
    }

    private List<FieldErrorDetail> extractHandlerMethodValidationDetails(
            HandlerMethodValidationException exception
    ) {
        List<FieldErrorDetail> details = new ArrayList<>();
        for (ParameterValidationResult result : exception.getParameterValidationResults()) {
            String parameterName = result.getMethodParameter().getParameterName();
            if (parameterName == null || parameterName.isBlank()) {
                parameterName = "parameter";
            }

            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                details.add(new FieldErrorDetail(parameterName, error.getDefaultMessage()));
            }
        }
        return details;
    }

    private String requestId(HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }

        String requestId = request.getHeader("X-Request-Id");
        if (requestId != null && !requestId.isBlank()) {
            return requestId;
        }

        return null;
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof TimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
