package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.service.TimeSlotService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/time-slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    public RsData<TimeSlotResponse> createTimeSlot(@RequestBody @Valid TimeSlotReqForm timeSlotReqForm) {
        TimeSlotResponse timeSlotResponse = timeSlotService.createTimeSlot(timeSlotReqForm);

        return new RsData<>(
                "200-3",
                "시간대가 등록되었습니다.",
                timeSlotResponse
        );
    }
}
