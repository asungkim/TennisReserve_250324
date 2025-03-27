package com.tennis.reserve.global.security;

import com.tennis.reserve.global.dto.RsData;
import com.tennis.reserve.global.standard.util.Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .formLogin(form -> form.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
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
