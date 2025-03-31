package com.tennis.reserve.domain.tennis.dto.response;

import com.tennis.reserve.domain.tennis.entity.Court;
import lombok.Builder;

import java.util.List;

@Builder
public record CourtResponse(
        Long id,
        String courtCode,
        String surfaceType,
        String environment,
        Long tennisCourtId,
        List<TimeSlotResponse> timeSlots
) {
    public static CourtResponse fromEntity(Court court) {
        return CourtResponse.builder()
                .id(court.getId())
                .courtCode(court.getCourtCode())
                .surfaceType(court.getSurfaceType().name())
                .environment(court.getEnvironment().name())
                .tennisCourtId(court.getTennisCourt().getId())
                .timeSlots(court.getTimeSlots()
                        .stream()
                        .map(TimeSlotResponse::fromEntity)
                        .toList())
                .build();
    }
}
