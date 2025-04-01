package com.tennis.reserve.domain.tennis.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TennisCourtReqForm(
        @NotBlank(message = "테니스장 이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "테니스장 위치는 필수 입력값입니다.")
        String location,

        @NotBlank(message = "이미지 URL은 필수 입력값입니다.")
        String imageUrl
) {
}
