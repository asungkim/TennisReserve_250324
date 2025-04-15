package com.tennis.reserve.domain.tennis.dto.response.timeSlot;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimeSlotResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        TimeSlotStatus status,
        String tennisCourtName,
        String courtCode
) {
    public static TimeSlotResponse fromEntity(TimeSlot timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime().withMinute(0).withSecond(0).withNano(0))
                .endTime(timeSlot.getEndTime().withMinute(0).withSecond(0).withNano(0))
                .status(timeSlot.getStatus())
                .tennisCourtName(timeSlot.getCourt().getTennisCourt().getName())
                .courtCode(timeSlot.getCourt().getCourtCode())
                .build();
    }
}
