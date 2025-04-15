package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
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
                "%s 에 %s 코트에 %s ~ %s 시간대가 등록되었습니다."
                        .formatted(res.tennisCourtName(),res.courtCode(),res.startTime(),res.endTime()),
                res
        );
    }

    // TODO : 목록 조회, 단건 조회, 수정, 삭제

//    @GetMapping
//    @PreAuthorize("hasRole('USER')")
//    @Transactional(readOnly = true)
//    public RsData<List<TimeSlotResponse>> getTimeSlotList(
//            @PathVariable Long tennisCourtId,
//            @PathVariable Long courtId
//    ) {
//        List<TimeSlotResponse> list = timeSlotService.getTimeSlotList(tennisCourtId, courtId);
//    }
}
