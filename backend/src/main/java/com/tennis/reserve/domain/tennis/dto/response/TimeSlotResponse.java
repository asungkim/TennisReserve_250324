package com.tennis.reserve.domain.tennis.dto.response;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimeSlotResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        TimeSlotStatus status
) {
    public static TimeSlotResponse fromEntity(TimeSlot timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(timeSlot.getStatus())
                .build();
    }
}
