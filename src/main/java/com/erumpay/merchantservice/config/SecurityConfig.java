package com.erumpay.merchantservice.config;

import com.erumpay.merchantservice.security.MerchantAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MerchantAuthenticationFilter merchantAuthenticationFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value()))
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpStatus.FORBIDDEN.value()))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/internal/**").hasRole("INTERNAL_SERVICE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/pg-admin/merchants/*").hasAnyRole("MERCHANT", "PG_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pg-admin/merchants/*").hasAnyRole("MERCHANT", "PG_ADMIN")
                        .requestMatchers("/api/v1/pg-admin/**").hasRole("PG_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(merchantAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
