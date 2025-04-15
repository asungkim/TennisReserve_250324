package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotListResponse;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotResponse;
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
        TimeSlotResponse res = timeSlotService.createTimeSlot(timeSlotReqForm, courtId);

        return new RsData<>(
                "200-3",
                "%s 의 %s 코트에 %s ~ %s 시간대가 등록되었습니다."
                        .formatted(res.tennisCourtName(), res.courtCode(), res.startTime(), res.endTime()),
                res
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public RsData<TimeSlotListResponse> getTimeSlotList(
            @PathVariable Long tennisCourtId,
            @PathVariable Long courtId
    ) {
        TimeSlotListResponse res = timeSlotService.getTimeSlotList(tennisCourtId, courtId);

        return new RsData<>(
                "200-4",
                "%s 의 %s 코트에 시간대 목록을 조회하였습니다."
                        .formatted(res.tennisCourtName(), res.courtCode()),
                res
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public RsData<TimeSlotResponse> getTimeSlot(
            @PathVariable Long tennisCourtId,
            @PathVariable Long courtId,
            @PathVariable Long id
    ) {
        TimeSlotResponse res = timeSlotService.getTimeSlot(tennisCourtId, courtId, id);

        return new RsData<>(
                "200-5",
                "%s 의 %s 코트에 %s ~ %s 시간대를 조회하였습니다."
                        .formatted(res.tennisCourtName(), res.courtCode(), res.startTime(), res.endTime()),
                res
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TimeSlotResponse> modifyTimeSlot(
            @PathVariable Long tennisCourtId,
            @PathVariable Long courtId,
            @PathVariable Long id,
            @RequestBody @Valid TimeSlotModifyReqForm modifyReqForm
    ) {
        TimeSlotResponse res = timeSlotService.modifyTimeSlot(tennisCourtId, courtId, id, modifyReqForm);

        return new RsData<>(
                "200-6",
                "%s 의 %s 코트의 특정 시간대를 %s ~ %s 로 수정하였습니다."
                        .formatted(res.tennisCourtName(), res.courtCode(), res.startTime(), res.endTime()),
                res
        );
    }
}
