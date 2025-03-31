package com.tennis.reserve.domain.tennis.dto.request;

import com.tennis.reserve.domain.tennis.enums.Environment;
import com.tennis.reserve.domain.tennis.enums.SurfaceType;
import org.springframework.lang.NonNull;

public record CourtReqForm(
        @NonNull String courtCode,
        @NonNull SurfaceType surfaceType,
        @NonNull Environment environment,
        @NonNull Long tennisCourtId
) {
}
