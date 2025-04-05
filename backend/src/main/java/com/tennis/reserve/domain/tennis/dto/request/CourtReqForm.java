package com.tennis.reserve.domain.tennis.dto.request;

import com.tennis.reserve.domain.tennis.enums.Environment;
import com.tennis.reserve.domain.tennis.enums.SurfaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourtReqForm(

        @NotBlank(message = "코트 코드는 필수 입력값입니다.")
        String courtCode,

        @NotNull(message = "표면 종류(SurfaceType)는 필수 입력값입니다.")
        SurfaceType surfaceType,

        @NotNull(message = "환경(Environment)은 필수 입력값입니다.")
        Environment environment
) {
}
