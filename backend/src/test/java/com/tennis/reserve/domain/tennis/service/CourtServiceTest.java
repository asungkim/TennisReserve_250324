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
}