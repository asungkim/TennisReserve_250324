package com.tennis.reserve.domain.tennis.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CourtModifyReqForm(
        @NotBlank(message = "코트 코드는 필수 입력값입니다.")
        String courtCode,

        @NotBlank(message = "표면 종류(SurfaceType)는 필수 입력값입니다.")
        String surfaceType,

        @NotBlank(message = "환경(Environment)은 필수 입력값입니다.")
        String environment
) {
}
