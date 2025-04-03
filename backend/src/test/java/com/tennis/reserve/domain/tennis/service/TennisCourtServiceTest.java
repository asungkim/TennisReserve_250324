package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.TennisCourtResponse;
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
class TennisCourtServiceTest {

    @Autowired
    private TennisCourtService tennisCourtService;

    @Test
    @DisplayName("테니스장 등록 성공")
    void create() {
        // given
        TennisCourtReqForm tennisCourtReqForm = new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test.url"
        );

        // when
        TennisCourtResponse tennisCourtResponse = tennisCourtService.createTennisCourt(tennisCourtReqForm);

        // then
        assertThat(tennisCourtResponse.name()).isEqualTo("양평누리 테니스장");
        assertThat(tennisCourtResponse.location()).isEqualTo("서울시 영등포구");
    }

    @Test
    @DisplayName("테니스장 목록 조회")
    void getList() {
        // given 테니스장 2개 등록
        tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        ));
        tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "올림픽 테니스장", "서울시 강남구", "http://test2.url"
        ));


        // when
        List<TennisCourtResponse> tennisCourts = tennisCourtService.getTennisCourts();


        // then
        assertThat(tennisCourts.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("테니스장 단건 조회")
    void getTennisCourt() {
        // given 테니스장 2개 등록
        tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        ));
        tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "올림픽 테니스장", "서울시 강남구", "http://test2.url"
        ));


        // when
        TennisCourtResponse tennisCourtResponse1 = tennisCourtService.getTennisCourt(1L);
        TennisCourtResponse tennisCourtResponse2 = tennisCourtService.getTennisCourt(2L);


        // then
        assertThat(tennisCourtResponse1.name()).isEqualTo("양평누리 테니스장");
        assertThat(tennisCourtResponse2.name()).isEqualTo("올림픽 테니스장");

    }



}