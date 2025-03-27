package com.tennis.reserve.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResBody {
    private MemberResBody items;
    private String accessToken;
}
