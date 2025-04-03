package com.tennis.reserve.domain.tennis.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TennisCourtModifyReqForm(
        @NotBlank(message = "테니스장 이름은 공백일 수 없습니다.")
        String name,

        @NotBlank(message = "위치는 공백일 수 없습니다.")
        String location,

        @NotBlank(message = "이미지 URL은 공백일 수 없습니다.")
        String imageUrl
) {}