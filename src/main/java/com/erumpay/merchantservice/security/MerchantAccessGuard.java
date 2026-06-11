package com.erumpay.merchantservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MerchantAccessGuard {

    public void check(Authentication authentication, Long merchantId) {
        boolean pgAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_PG_ADMIN".equals(authority.getAuthority()));
        if (pgAdmin) {
            return;
        }
        if (authentication.getPrincipal() instanceof MerchantPrincipal principal
                && "MERCHANT".equals(principal.role())
                && merchantId.equals(principal.merchantId())) {
            return;
        }
        throw new AccessDeniedException("자기 가맹점 정보만 접근할 수 있습니다.");
    }
}
