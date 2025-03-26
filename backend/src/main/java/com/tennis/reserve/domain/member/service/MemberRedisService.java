package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberRedisService {
    private final MemberRedisRepository memberRedisRepository;
    private static final String REFRESH_TOKEN_KEY = "refreshToken:";

    @Value("${custom.refreshToken.expire-seconds}")
    private Long expireSeconds;

    public void save(Member member, String token) {
        String key = REFRESH_TOKEN_KEY + member.getId();
        memberRedisRepository.save(key, token, expireSeconds);
    }

    public Optional<String> get(Member member) {
        String key = REFRESH_TOKEN_KEY + member.getId();
        return memberRedisRepository.find(key);
    }

    public void delete(Member member) {
        String key = REFRESH_TOKEN_KEY + member.getId();
        memberRedisRepository.delete(key);
    }

    public boolean isValid(Member member, String token) {
        return get(member)
                .map(stored -> stored.equals(token))
                .orElse(false);
    }
}
