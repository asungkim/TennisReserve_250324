package com.tennis.reserve.global.security;

import com.tennis.reserve.global.dto.RsData;
import com.tennis.reserve.global.standard.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 여기서 permitAll()은 WHITELIST만 허용
                        .requestMatchers("/api/members/login", "/api/members/join").permitAll()
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .headers(headers -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .formLogin(form -> form.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // @PreAuthorize 예외 처리
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                // isAuthenticated()
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=UTF-8");
                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    Util.Json.toString(
                                                            new RsData<>("401-1", "잘못된 인증키입니다.")
                                                    )
                                            );
                                        }
                                )
                                // hasRole()
                                .accessDeniedHandler(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=UTF-8");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    Util.Json.toString(
                                                            new RsData<>("403-1", "접근 권한이 없습니다.")
                                                    )
                                            );
                                        }
                                )

                );

        return http.build();
    }
}
