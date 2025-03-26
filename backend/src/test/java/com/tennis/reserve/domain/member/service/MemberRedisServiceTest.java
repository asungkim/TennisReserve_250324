package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.entity.Role;
import com.tennis.reserve.global.BaseTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@BaseTestConfig
class MemberRedisServiceTest {

    @Autowired
    private MemberRedisService memberRedisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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

    @Test
    @DisplayName("TestContainers 정상 동작 확인")
    void basic1() {
        String pingResponse = redisTemplate.execute(RedisConnectionCommands::ping);
        assertThat(pingResponse).isEqualTo("PONG");
    }

    @Test
    @DisplayName("redis : RedisTemplate : 저장 및 조회 테스트")
    void basic2() {
        // Given
        String key = "redis-template-key";
        String value = "redis-template-value";

        // When
        redisTemplate.opsForValue().set(key, value);
        String storedValue = redisTemplate.opsForValue().get(key);

        // Then
        assertThat(storedValue).isEqualTo(value);
    }

    @Test
    @DisplayName("refreshToken 저장 및 조회")
    void saveAndGet() {
        // given
        String refreshToken = UUID.randomUUID().toString();


        // when
        memberRedisService.save(member, refreshToken);
        Optional<String> result = memberRedisService.get(member);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("refreshToken 삭제")
    void delete() {
        // given
        String refreshToken = UUID.randomUUID().toString();


        // when 1
        memberRedisService.save(member, refreshToken);
        Optional<String> result = memberRedisService.get(member);

        // then 1
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshToken);

        // when 2 (삭제)
        memberRedisService.delete(member);

        // then 2 (비어있음)
        Optional<String> deleted = memberRedisService.get(member);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("refreshToken 유효성 검사 - 성공")
    void isValid1() {
        // given
        String token = UUID.randomUUID().toString();
        memberRedisService.save(member, token);

        // when
        boolean isValid = memberRedisService.isValid(member, token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("refreshToken 유효성 검사 - 실패")
    void isValid2() {
        // given
        String token = UUID.randomUUID().toString();
        memberRedisService.save(member, token);

        // when
        boolean isValid = memberRedisService.isValid(member, "wrong-token");

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("TTL 만료 테스트")
    void isExpired() throws InterruptedException {
        // given
        String token = UUID.randomUUID().toString();
        memberRedisService.save(member, token);

        // 현재 refreshToken 3초)
        Thread.sleep(4000);

        // when
        Optional<String> result = memberRedisService.get(member);

        // then
        assertThat(result).isEmpty();

    }


}