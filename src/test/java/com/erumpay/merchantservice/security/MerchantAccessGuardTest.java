package com.erumpay.merchantservice.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class MerchantAccessGuardTest {

    private final MerchantAccessGuard guard = new MerchantAccessGuard();

    @Test
    void merchantCanAccessOwnMerchantOnly() {
        var authentication = new UsernamePasswordAuthenticationToken(
                new MerchantPrincipal(1L, 10L, "MERCHANT"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_MERCHANT"))
        );

        assertThatCode(() -> guard.check(authentication, 10L)).doesNotThrowAnyException();
        assertThatThrownBy(() -> guard.check(authentication, 11L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void pgAdminCanAccessEveryMerchant() {
        var authentication = new UsernamePasswordAuthenticationToken(
                new MerchantPrincipal(1L, null, "PG_ADMIN"),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PG_ADMIN"))
        );

        assertThatCode(() -> guard.check(authentication, 999L)).doesNotThrowAnyException();
    }
}
