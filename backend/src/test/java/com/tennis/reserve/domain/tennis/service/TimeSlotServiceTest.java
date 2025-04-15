package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TimeSlotResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TimeSlotServiceTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private TennisCourtService tennisCourtService;

    private Long courtId;

    @BeforeEach
    void setUp() {
        Long tennisCourtId = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        )).id();

        courtId = courtService.createCourt(new CourtReqForm("A", "HARD", "OUTDOOR"),tennisCourtId).id();
    }

    @Test
    @DisplayName("시간대 등록")
    void create() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 4, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 4, 1, 12, 0);
        TimeSlotReqForm timeSlotReqForm = new TimeSlotReqForm(start, end);

        // when
        TimeSlotResponse timeSlotResponse = timeSlotService.createTimeSlot(timeSlotReqForm,courtId);

        // then
        assertThat(timeSlotResponse.startTime()).isEqualTo(start);
        assertThat(timeSlotResponse.endTime()).isEqualTo(end);
        assertThat(timeSlotResponse.tennisCourtName()).isEqualTo("양평누리 테니스장");
        assertThat(timeSlotResponse.courtCode()).isEqualTo("A");
    }

}