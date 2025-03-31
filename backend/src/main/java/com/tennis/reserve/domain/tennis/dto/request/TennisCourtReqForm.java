package com.tennis.reserve.domain.tennis.dto.request;

import org.springframework.lang.NonNull;

public record TennisCourtReqForm(
        @NonNull String name,
        @NonNull String location,
        @NonNull String imageUrl
) {
}
