package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.enums.Environment;
import com.tennis.reserve.domain.tennis.enums.SurfaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourtServiceTest {

    @Autowired
    private TennisCourtService tennisCourtService;

    @Autowired
    private CourtService courtService;

    private Long tennisCourtId;

    @BeforeEach
    void setUp() {
        tennisCourtId = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        )).id();
    }

    @Test
    @DisplayName("테니스 코트 등록")
    void create() {
        // given
        CourtReqForm courtReqForm =
                new CourtReqForm("A", SurfaceType.HARD, Environment.OUTDOOR);

        // when
        CourtResponse courtResponse = courtService.createCourt(courtReqForm, tennisCourtId);

        // then
        assertThat(courtResponse.courtCode()).isEqualTo("A");
        assertThat(courtResponse.surfaceType()).isEqualTo("HARD");
        assertThat(courtResponse.tennisCourtId()).isEqualTo(tennisCourtId);
    }

    @Test
    @DisplayName("테니스 코트 다건 조회")
    void getCourts() {
        // given
        courtService.createCourt(new CourtReqForm("A", SurfaceType.HARD, Environment.OUTDOOR), tennisCourtId);
        courtService.createCourt(new CourtReqForm("B", SurfaceType.CLAY, Environment.INDOOR), tennisCourtId);

        // when
        List<CourtResponse> courts = courtService.getCourts(tennisCourtId);


        // then
        assertThat(courts).hasSize(2);
        assertThat(courts).extracting("courtCode")
                .containsExactlyInAnyOrder("A", "B");
        assertThat(courts.get(0).tennisCourtId()).isEqualTo(tennisCourtId);
        assertThat(courts.get(0).timeSlots()).isEmpty();
    }

    @Test
    @DisplayName("테니스 코트 단건 조회")
    void getCourt() {
        // given
        CourtResponse savedCourt = courtService.createCourt(
                new CourtReqForm("C", SurfaceType.HARD, Environment.OUTDOOR),
                tennisCourtId
        );

        // when
        CourtResponse foundCourt = courtService.getCourt(tennisCourtId, savedCourt.id());


        // then
        assertThat(foundCourt.id()).isEqualTo(savedCourt.id());
        assertThat(foundCourt.courtCode()).isEqualTo("C");
        assertThat(foundCourt.surfaceType()).isEqualTo("HARD");
    }
}