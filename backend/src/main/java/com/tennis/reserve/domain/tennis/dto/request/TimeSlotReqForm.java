package com.tennis.reserve.domain.tennis.dto.request;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record TimeSlotReqForm(
        @NonNull Long courtId,
        @NonNull LocalDateTime startTime,
        @NonNull LocalDateTime endTime
) {
}
