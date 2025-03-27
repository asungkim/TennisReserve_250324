package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.AuthToken;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MemberAuthServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .username("testUser")
                .password("!password1")
                .nickname("testUser1")
                .email("test@example.com")
                .role(Role.USER)
                .build();
    }

    // 토큰 생성
    @Test
    @DisplayName("토큰 생성")
    void generateToken() {

        AuthToken authToken = memberAuthService.generateAuthToken(member);
        assertThat(authToken).isNotNull();
        assertThat(authToken.accessToken()).isNotBlank();
        assertThat(authToken.refreshToken()).isNotBlank();

        Map<String, Object> payload = memberAuthService.getPayload(authToken.accessToken());

        Long id = ((Number) payload.get("id")).longValue();
        assertThat(id).isEqualTo(1L);
        assertThat(payload.get("username")).isEqualTo(member.getUsername());
        assertThat(payload.get("nickname")).isEqualTo(member.getNickname());
        assertThat(payload.get("role")).isEqualTo(member.getRole().name());


    }
}