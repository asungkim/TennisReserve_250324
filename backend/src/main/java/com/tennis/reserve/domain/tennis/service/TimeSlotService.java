package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.court.CourtResponse;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotItem;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotListResponse;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.entity.Court;
import com.tennis.reserve.domain.tennis.entity.TimeSlot;
import com.tennis.reserve.domain.tennis.repository.TimeSlotRepository;
import com.tennis.reserve.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final CourtService courtService;

    public TimeSlotResponse createTimeSlot(TimeSlotReqForm timeSlotReqForm, Long courtId) {

        // 검증 -> 해당 코트에 이미 겹치는 시간대가 등록되어있는지
        Court court = validateDuplicateTimeSlot(timeSlotReqForm, courtId);

        TimeSlot timeSlot = TimeSlot.builder()
                .startTime(timeSlotReqForm.startTime())
                .endTime(timeSlotReqForm.endTime())
                .court(court)
                .build();

        court.addTimeSlot(timeSlot);
        timeSlotRepository.save(timeSlot);

        return TimeSlotResponse.fromEntity(timeSlot);
    }

    private Court validateDuplicateTimeSlot(TimeSlotReqForm timeSlotReqForm, Long courtId) {
        Court court = courtService.findById(courtId);
        LocalTime newStart = timeSlotReqForm.startTime();
        LocalTime newEnd = timeSlotReqForm.endTime();

        boolean isOverlapped = court.getTimeSlots().stream()
                .anyMatch(existingSlot -> {
                    LocalTime existingStart = existingSlot.getStartTime();
                    LocalTime existingEnd = existingSlot.getEndTime();

                    return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                });

        if (isOverlapped) {
            throw new ServiceException("409-3", "겹치는 시간대가 이미 생성되어 있습니다.");
        }

        return court;
    }

    @Transactional(readOnly = true)
    public TimeSlotListResponse getTimeSlotList(Long tennisCourtId, Long courtId) {
        // TennisCourtId와 CourtId를 통해 timeSlot 목록을 조회하고, TimeSlotItem DTO로 변경
        List<TimeSlotItem> timeSlotItems = timeSlotRepository.findByCourt_TennisCourt_IdAndCourt_Id(tennisCourtId, courtId)
                .stream().map(TimeSlotItem::fromEntity).toList();

        // 코트 정보를 가져옴
        CourtResponse court = courtService.getCourt(tennisCourtId, courtId);

        return TimeSlotListResponse.of(tennisCourtId, courtId, court.courtCode(), court.tennisCourtName(), timeSlotItems);
    }

    @Transactional(readOnly = true)
    public TimeSlotResponse getTimeSlot(Long tennisCourtId, Long courtId, Long id) {
        TimeSlot timeSlot = timeSlotRepository.findByCourt_TennisCourt_IdAndCourt_IdAndId(tennisCourtId, courtId, id)
                .orElseThrow(() -> new ServiceException("404-2", "해당 시간대를 찾을 수 없습니다."));

        return TimeSlotResponse.fromEntity(timeSlot);
    }

}
