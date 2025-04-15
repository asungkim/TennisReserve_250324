package com.tennis.reserve.domain.tennis.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record TimeSlotModifyReqForm(
        @NotNull(message = "시작 시간은 필수 입력값입니다.")
        LocalTime startTime,

        @NotNull(message = "종료 시간은 필수 입력값입니다.")
        LocalTime endTime,

        String status
) {
}
