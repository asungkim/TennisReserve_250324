package com.tennis.reserve.domain.tennis.dto.response.tennisCourt;

import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import lombok.Builder;

@Builder
public record TennisCourtItem(
        Long id,
        String name,
        String location,
        String imageUrl
) {
    public static TennisCourtItem fromEntity(TennisCourt tennisCourt) {
        return TennisCourtItem.builder()
                .id(tennisCourt.getId())
                .name(tennisCourt.getName())
                .location(tennisCourt.getLocation())
                .imageUrl(tennisCourt.getImageUrl())
                .build();
    }
}
