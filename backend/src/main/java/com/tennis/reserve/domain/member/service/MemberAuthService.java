package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.AuthToken;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.entity.Role;
import com.tennis.reserve.global.security.SecurityUser;
import com.tennis.reserve.global.standard.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberAuthService {

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


    public String generateAccessToken(Member member) {
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


    public boolean isValidToken(String token) {
        return Util.Jwt.isValidToken(keyString, token);
    }

    public void setLogin(Member actor) {
        UserDetails user = new SecurityUser(actor.getId(), actor.getUsername(), "", "", actor.getRole(), List.of());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    public Optional<Member> getMemberByAccessToken(String accessToken) {
        Map<String, Object> payload = getPayload(accessToken);

        if (payload == null) {
            return Optional.empty();
        }

        Long id = (Long) payload.get("id");
        String username = (String) payload.get("username");
        String nickname = (String) payload.get("nickname");
        Role role = (Role) payload.get("role");

        return Optional.of(
                Member.builder()
                        .id(id)
                        .username(username)
                        .nickname(nickname)
                        .role(role)
                        .build()
        );
    }

    public Optional<Member> getValidMemberByAccessToken(String accessToken) {
        if (!isValidToken(accessToken)) {
            return Optional.empty();
        }
        return getMemberByAccessToken(accessToken);
    }
}
