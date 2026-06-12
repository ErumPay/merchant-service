package com.erumpay.merchantservice.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class MerchantAuthenticationFilterTest {

    private final TestableMerchantAuthenticationFilter filter =
            new TestableMerchantAuthenticationFilter();

    @Test
    void skipsAuthenticationForApplicationApis() {
        assertThat(filter.shouldSkip("/api/v1/qr/generate")).isTrue();
        assertThat(filter.shouldSkip("/api/v1/payments")).isTrue();
        assertThat(filter.shouldSkip("/actuator/health")).isTrue();
        assertThat(filter.shouldSkip("/internal/v1/merchants/api-key/validate")).isTrue();
    }

    @Test
    void appliesAuthenticationOnlyToPgAdminApis() {
        assertThat(filter.shouldSkip("/api/v1/pg-admin/merchants/1")).isFalse();
        assertThat(filter.shouldSkip("/internal/v1/merchants/1/status")).isTrue();
    }

    private static class TestableMerchantAuthenticationFilter
            extends MerchantAuthenticationFilter {

        TestableMerchantAuthenticationFilter() {
            super(null);
        }

        boolean shouldSkip(String path) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(path);
            return shouldNotFilter(request);
        }
    }
}
