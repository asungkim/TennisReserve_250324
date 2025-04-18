package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TimeSlotModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TimeSlotReqForm;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotListResponse;
import com.tennis.reserve.domain.tennis.dto.response.timeSlot.TimeSlotResponse;
import com.tennis.reserve.domain.tennis.enums.TimeSlotStatus;
import com.tennis.reserve.domain.tennis.repository.TimeSlotRepository;
import com.tennis.reserve.global.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private Long tennisCourtId;
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @BeforeEach
    void setUp() {
        tennisCourtId = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        )).id();

        courtId = courtService.createCourt(new CourtReqForm("A", "HARD", "OUTDOOR"), tennisCourtId).id();
    }

    @Test
    @DisplayName("시간대 등록")
    void create() {
        // given
        LocalTime start = LocalTime.of(10, 0, 0);
        LocalTime end = LocalTime.of(12, 0, 0);
        TimeSlotReqForm timeSlotReqForm = new TimeSlotReqForm(start, end);

        // when
        TimeSlotResponse timeSlotResponse = timeSlotService.createTimeSlot(timeSlotReqForm, courtId);

        // then
        assertThat(timeSlotResponse.startTime()).isEqualTo(start);
        assertThat(timeSlotResponse.endTime()).isEqualTo(end);
        assertThat(timeSlotResponse.tennisCourtName()).isEqualTo("양평누리 테니스장");
        assertThat(timeSlotResponse.courtCode()).isEqualTo("A");
    }

    @Test
    @DisplayName("시간대 목록 조회")
    void getList() {
        // given
        LocalTime start1 = LocalTime.of(10, 0, 0);
        LocalTime end1 = LocalTime.of(12, 0, 0);
        LocalTime start2 = LocalTime.of(12, 0, 0);
        LocalTime end2 = LocalTime.of(14, 0, 0);
        TimeSlotReqForm timeSlotReqForm1 = new TimeSlotReqForm(start1, end1);
        TimeSlotReqForm timeSlotReqForm2 = new TimeSlotReqForm(start2, end2);

        timeSlotService.createTimeSlot(timeSlotReqForm1, courtId);
        timeSlotService.createTimeSlot(timeSlotReqForm2, courtId);

        // when
        TimeSlotListResponse timeSlotRes = timeSlotService.getTimeSlotList(tennisCourtId, courtId);

        // then
        assertThat(timeSlotRes.timeSlots().size()).isEqualTo(2);
        assertThat(timeSlotRes.tennisCourtId()).isEqualTo(tennisCourtId);
        assertThat(timeSlotRes.courtId()).isEqualTo(courtId);
    }


    @Test
    @DisplayName("시간대 단건 조회")
    void getTimeSlot() {
        // given
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(10, 0, 0);
        TimeSlotReqForm timeSlotReqForm = new TimeSlotReqForm(start, end);
        TimeSlotResponse created = timeSlotService.createTimeSlot(timeSlotReqForm, courtId);

        // when
        TimeSlotResponse found = timeSlotService.getTimeSlot(tennisCourtId, courtId, created.id());

        // then
        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.startTime()).isEqualTo(start);
        assertThat(found.endTime()).isEqualTo(end);
        assertThat(found.tennisCourtName()).isEqualTo("양평누리 테니스장");
        assertThat(found.courtCode()).isEqualTo("A");
    }

    @Test
    @DisplayName("시간대 수정")
    void modify() {
        // given
        LocalTime start = LocalTime.of(9, 0, 0);
        LocalTime end = LocalTime.of(11, 0, 0);
        TimeSlotReqForm timeSlotReqForm = new TimeSlotReqForm(start, end);
        TimeSlotResponse created = timeSlotService.createTimeSlot(timeSlotReqForm, courtId);

        // when
        LocalTime newStart = LocalTime.of(13, 0, 0);
        LocalTime newEnd = LocalTime.of(15, 0, 0);
        TimeSlotModifyReqForm modifyReqForm = new TimeSlotModifyReqForm(newStart, newEnd, "RESERVED");
        TimeSlotResponse modified = timeSlotService.modifyTimeSlot(tennisCourtId, courtId, created.id(), modifyReqForm);

        // then
        assertThat(created.startTime()).isEqualTo(start);
        assertThat(created.endTime()).isEqualTo(end);
        assertThat(created.status()).isEqualTo(TimeSlotStatus.AVAILABLE);
        assertThat(modified.startTime()).isEqualTo(newStart);
        assertThat(modified.endTime()).isEqualTo(newEnd);
        assertThat(modified.status()).isEqualTo(TimeSlotStatus.RESERVED);
    }

    @Test
    @DisplayName("시간대 삭제")
    void delete() {
        // given
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(12, 0);
        TimeSlotReqForm reqForm = new TimeSlotReqForm(start, end);

        TimeSlotResponse created = timeSlotService.createTimeSlot(reqForm, courtId);

        // when
        String result = timeSlotService.deleteTimeSlot(tennisCourtId, courtId, created.id());

        // then
        assertThat(result).contains("양평누리 테니스장");
        assertThat(result).contains("A");
        assertThat(result).contains("10:00");
        assertThat(result).contains("12:00");

        assertThatThrownBy(() -> timeSlotService.findById(created.id()))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("해당 시간대는 존재하지 않습니다.");
    }
}