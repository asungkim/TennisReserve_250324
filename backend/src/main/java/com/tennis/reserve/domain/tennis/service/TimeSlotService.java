package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.entity.Court;
import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final CourtService courtService;

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotReqForm timeSlotReqForm) {

        Court court = courtService.findById(timeSlotReqForm.courtId());

        TimeSlot timeSlot = TimeSlot.builder()
                .startTime(timeSlotReqForm.startTime())
                .endTime(timeSlotReqForm.endTime())
                .court(court)
                .build();

        court.addTimeSlot(timeSlot);
        timeSlotRepository.save(timeSlot);

        return TimeSlotResponse.fromEntity(timeSlot);
    }
}
