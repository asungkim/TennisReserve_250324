package com.tennis.reserve.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginReqForm(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
