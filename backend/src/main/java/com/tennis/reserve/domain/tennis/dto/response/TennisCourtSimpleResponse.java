package com.tennis.reserve.domain.tennis.dto.response;

import com.tennis.reserve.domain.tennis.entity.TennisCourt;
import lombok.Builder;

@Builder
public record TennisCourtSimpleResponse(
        Long id,
        String name,
        String location,
        String imageUrl
) {
    public static TennisCourtSimpleResponse fromEntity(TennisCourt tennisCourt) {
        return TennisCourtSimpleResponse.builder()
                .id(tennisCourt.getId())
                .name(tennisCourt.getName())
                .location(tennisCourt.getLocation())
                .imageUrl(tennisCourt.getImageUrl())
                .build();
    }
}
