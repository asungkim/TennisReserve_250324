package com.tennis.reserve.domain.tennis.dto.response.timeSlot;

import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record TimeSlotResponse(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        TimeSlotStatus status,
        String tennisCourtName,
        String courtCode
) {
    public static TimeSlotResponse fromEntity(TimeSlot timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .status(timeSlot.getStatus())
                .tennisCourtName(timeSlot.getCourt().getTennisCourt().getName())
                .courtCode(timeSlot.getCourt().getCourtCode())
                .build();
    }
}
