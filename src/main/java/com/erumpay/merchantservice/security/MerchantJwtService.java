package com.erumpay.merchantservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MerchantJwtService {

    private final SecretKey secretKey;

    public MerchantJwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public MerchantPrincipal parseAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!"ACCESS".equals(claims.get("tokenType", String.class))) {
            throw new IllegalArgumentException("Access token required");
        }
        return new MerchantPrincipal(
                claims.get("accountId", Long.class),
                claims.get("merchantId", Long.class),
                claims.get("role", String.class)
        );
    }
}
