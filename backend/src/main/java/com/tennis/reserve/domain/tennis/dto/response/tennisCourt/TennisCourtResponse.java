package com.tennis.reserve.domain.tennis.dto.response.tennisCourt;

import com.tennis.reserve.domain.tennis.dto.response.court.CourtItem;
import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import lombok.Builder;

import java.util.List;

@Builder
public record TennisCourtResponse(
        Long id,
        String name,
        String location,
        String imageUrl,
        List<CourtItem> courts
) {
    public static TennisCourtResponse fromEntity(TennisCourt tennisCourt) {
        return TennisCourtResponse.builder()
                .id(tennisCourt.getId())
                .name(tennisCourt.getName())
                .location(tennisCourt.getLocation())
                .imageUrl(tennisCourt.getImageUrl())
                .courts(tennisCourt.getCourts()
                        .stream()
                        .map(CourtItem::fromEntity)
                        .toList())
                .build();
    }
}
