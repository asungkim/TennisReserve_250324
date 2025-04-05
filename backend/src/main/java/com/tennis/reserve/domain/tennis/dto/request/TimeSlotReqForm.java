package com.tennis.reserve.domain.tennis.dto.request;


import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TimeSlotReqForm(

        @NotNull(message = "시작 시간은 필수 입력값입니다.")
        LocalDateTime startTime,

        @NotNull(message = "종료 시간은 필수 입력값입니다.")
        LocalDateTime endTime
) {
}
