package com.tennis.reserve.domain.tennis.dto.response.timeSlot;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record TimeSlotItem(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        TimeSlotStatus status
) {
    public static TimeSlotItem fromEntity(TimeSlot timeSlot) {
        return TimeSlotItem.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(timeSlot.getStatus())
                .build();
    }
}
