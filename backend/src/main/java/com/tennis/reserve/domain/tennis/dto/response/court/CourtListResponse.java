package com.tennis.reserve.domain.tennis.dto.response.court;

import java.util.List;

public record CourtListResponse(
        Long tennisCourtId,
        String tennisCourtName,
        List<CourtItem> courtList
) {
    public static CourtListResponse of(Long tennisCourtId, String tennisCourtName, List<CourtItem> courtList) {
        return new CourtListResponse(tennisCourtId, tennisCourtName, courtList);
    }
}
