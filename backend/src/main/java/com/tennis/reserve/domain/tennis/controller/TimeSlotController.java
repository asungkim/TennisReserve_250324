package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.service.TimeSlotService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TimeSlotResponse> createTimeSlot(
            @PathVariable Long tennisCourtId,
            @PathVariable Long courtId,
            @RequestBody @Valid TimeSlotReqForm timeSlotReqForm) {
        TimeSlotResponse timeSlotResponse = timeSlotService.createTimeSlot(timeSlotReqForm,courtId);

        return new RsData<>(
                "200-3",
                "시간대가 등록되었습니다.",
                timeSlotResponse
        );
    }
}
