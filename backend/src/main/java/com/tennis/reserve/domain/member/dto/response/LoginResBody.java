package com.tennis.reserve.domain.member.dto.response;

import lombok.Builder;


@Builder
public record LoginResBody(
        MemberResBody items,
        String accessToken
) {
}
