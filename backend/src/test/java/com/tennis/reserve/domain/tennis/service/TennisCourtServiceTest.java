package com.tennis.reserve.domain.tennis.service;

import com.tennis.reserve.domain.tennis.dto.request.TennisCourtModifyReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.tennisCourt.TennisCourtResponse;
import com.tennis.reserve.domain.tennis.dto.response.tennisCourt.TennisCourtItem;
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
        TennisCourtResponse t1 = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        ));
        TennisCourtResponse t2 = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "올림픽 테니스장", "서울시 강남구", "http://test2.url"
        ));


        // when
        TennisCourtResponse tennisCourtResponse1 = tennisCourtService.getTennisCourt(t1.id());
        TennisCourtResponse tennisCourtResponse2 = tennisCourtService.getTennisCourt(t2.id());


        // then
        assertThat(tennisCourtResponse1.name()).isEqualTo("양평누리 테니스장");
        assertThat(tennisCourtResponse2.name()).isEqualTo("올림픽 테니스장");

    }

    @Test
    @DisplayName("테니스장 정보 수정")
    void modify() {
        // given 테니스장 등록
        TennisCourtResponse prevResponse = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        ));


        // when 수정하면
        TennisCourtModifyReqForm modifyReqForm =
                new TennisCourtModifyReqForm("목동 테니스장", "서울시 양천구", "http://modify.url");
        TennisCourtItem nextResponse = tennisCourtService.modifyTennisCourt(modifyReqForm, prevResponse.id());


        // then
        assertThat(prevResponse.name()).isEqualTo("양평누리 테니스장");
        assertThat(prevResponse.location()).isEqualTo("서울시 영등포구");
        assertThat(prevResponse.imageUrl()).isEqualTo("http://test1.url");
        assertThat(nextResponse.name()).isEqualTo("목동 테니스장");
        assertThat(nextResponse.location()).isEqualTo("서울시 양천구");
        assertThat(nextResponse.imageUrl()).isEqualTo("http://modify.url");
    }


}