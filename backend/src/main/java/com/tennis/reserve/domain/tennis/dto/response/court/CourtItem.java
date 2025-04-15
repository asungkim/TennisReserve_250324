package com.tennis.reserve.domain.tennis.dto.response.court;

import com.tennis.reserve.domain.tennis.entity.Court;
import lombok.Builder;

@Builder
public record CourtItem(
        Long id,
        String courtCode,
        String surfaceType,
        String environment
) {
    public static CourtItem fromEntity(Court court) {
        return CourtItem.builder()
                .id(court.getId())
                .courtCode(court.getCourtCode())
                .surfaceType(court.getSurfaceType().name())
                .environment(court.getEnvironment().name())
                .build();
    }
}
