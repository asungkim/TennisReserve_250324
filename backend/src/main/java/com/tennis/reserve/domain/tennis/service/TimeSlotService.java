package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.entity.Court;
import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.repository.TimeSlotRepository;
import com.tennis.reserve.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final CourtService courtService;

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotReqForm timeSlotReqForm) {

        // 검증 -> 해당 코트에 이미 겹치는 시간대가 등록되어있는지
        validateDuplicateTimeSlot(timeSlotReqForm);

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

    private void validateDuplicateTimeSlot(TimeSlotReqForm timeSlotReqForm) {
        Court court = courtService.findById(timeSlotReqForm.courtId());
        LocalDateTime newStart = timeSlotReqForm.startTime();
        LocalDateTime newEnd = timeSlotReqForm.endTime();

        boolean isOverlapped = court.getTimeSlots().stream()
                .anyMatch(existingSlot -> {
                    LocalDateTime existingStart = existingSlot.getStartTime();
                    LocalDateTime existingEnd = existingSlot.getEndTime();

                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });

        if (isOverlapped) {
            throw new ServiceException("409-3", "겹치는 시간대가 이미 생성되어 있습니다.");
        }
    }
}
