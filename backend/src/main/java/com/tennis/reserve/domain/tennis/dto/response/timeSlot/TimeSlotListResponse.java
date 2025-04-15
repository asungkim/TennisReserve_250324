package com.tennis.reserve.domain.tennis.dto.response.timeSlot;

import java.util.List;

public record TimeSlotListResponse(
        Long courtId,
        String courtCode,
        String tennisCourtName,
        List<TimeSlotItem> timeSlots
) {
    public static TimeSlotListResponse of(Long courtId, String courtCode, String tennisCourtName, List<TimeSlotItem> timeSlots) {
        return new TimeSlotListResponse(courtId, courtCode, tennisCourtName, timeSlots);
    }
}