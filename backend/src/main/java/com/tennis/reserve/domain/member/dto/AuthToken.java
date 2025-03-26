package com.tennis.reserve.domain.member.dto;

import lombok.Builder;

@Builder
public record AuthToken(
        String accessToken,
        String refreshToken
) {
}
