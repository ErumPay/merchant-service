package com.erumpay.merchantservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erumpay.merchantservice.global.exception.GlobalExceptionHandler;
import com.erumpay.merchantservice.security.MerchantAuthenticationFilter;
import com.erumpay.merchantservice.service.MerchantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = InternalMerchantController.class,
        properties = {
                "jwt.secret=test-secret-test-secret-test-secret-1234",
                "internal.api-key=test-internal-key"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class InternalMerchantControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MerchantService merchantService;

    @MockitoBean
    private MerchantAuthenticationFilter merchantAuthenticationFilter;

    @Test
    @DisplayName("내부 가맹점 생성 API의 멱등키 누락은 MER-IDM-001로 응답한다")
    void createInternalMerchantWithoutIdempotencyKey() throws Exception {
        mockMvc.perform(post("/internal/v1/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MER-IDM-001"))
                .andExpect(jsonPath("$.reason").value("IDEMPOTENCY_KEY_REQUIRED"))
                .andExpect(jsonPath("$.message").value("멱등키가 필요합니다."));
    }

    @Test
    @DisplayName("내부 가맹점 생성 API의 본문 검증 실패는 MER-REQ-001로 응답한다")
    void createInternalMerchantWithInvalidBody() throws Exception {
        mockMvc.perform(post("/internal/v1/merchants")
                        .header("Idempotency-Key", "idem-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MER-REQ-001"))
                .andExpect(jsonPath("$.reason").value("MERCHANT_INVALID_REQUEST"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("내부 가맹점 생성 API의 조합된 정산 계좌 길이 초과는 MER-REQ-001로 응답한다")
    void createInternalMerchantWithTooLongSettlementAccount() throws Exception {
        String body = """
                {
                  "business_number": "999-22-11111",
                  "merchant_name": "내부가맹점",
                  "mcc_code": "5812",
                  "representative_name": "홍길동",
                  "contact_phone": "010-1111-2222",
                  "business_address": "서울특별시 강남구 테헤란로 123",
                  "settlement_account": "123456789012345678901234567890123456789012345678901234567890",
                  "bank_name": "12345678901234567890123456789012345678901234567890",
                  "service_name": "테스트서비스"
                }
                """;

        mockMvc.perform(post("/internal/v1/merchants")
                        .header("Idempotency-Key", "idem-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MER-REQ-001"))
                .andExpect(jsonPath("$.reason").value("MERCHANT_INVALID_REQUEST"));
    }

    private String validRequestBody() {
        return """
                {
                  "business_number": "999-22-11111",
                  "merchant_name": "내부가맹점",
                  "mcc_code": "5812",
                  "representative_name": "홍길동",
                  "contact_phone": "010-1111-2222",
                  "business_address": "서울특별시 강남구 테헤란로 123",
                  "settlement_account": "123-456",
                  "bank_name": "국민",
                  "service_name": "테스트서비스"
                }
                """;
    }
}
