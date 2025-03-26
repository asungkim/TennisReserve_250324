package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.AuthToken;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.global.standard.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    public AuthToken generateAuthToken(Member member) {
        String refreshToken = generateRefreshToken();
        String accessToken = generateAccessToken(member);

        return AuthToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Map<String, Object> getPayload(String token) {
        return Util.Jwt.getPayload(keyString, token);
    }


    private String generateAccessToken(Member member) {
        return Util.Jwt.createToken(
                keyString,
                expireSeconds,
                Map.of(
                        "id", member.getId(),
                        "username", member.getUsername(),
                        "nickname", member.getNickname(),
                        "role", member.getRole()
                ));
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }


}
