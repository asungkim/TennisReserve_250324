package com.tennis.reserve.domain.member.dto.response;

import lombok.Getter;

@Getter
public class LoginResBody {
    private MemberResBody item;
    private String accessToken;
}
