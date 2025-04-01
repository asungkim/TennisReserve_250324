package com.tennis.reserve.domain.tennis.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class TennisCourtControllerTest {

    @Autowired
    private MockMvc mvc;


    private ResultActions createTennisCourtRequest(String name, String location, String imageUrl) throws Exception {
        return mvc.perform(post("/api/tennis-courts")
                        .content("""
                                {
                                    "name": "%s",
                                    "location": "%s",
                                    "imageUrl": "%s"
                                }
                                """.formatted(name, location, imageUrl).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());
    }

    @Test
    @DisplayName("테니스장 등록 성공")
    void create1() throws Exception {
        // given
        String name = "양평누리 테니스장";
        String location = "서울 영등포구";
        String imageUrl = "http://image.url";

        // when
        ResultActions result = createTennisCourtRequest(name, location, imageUrl);

        // then
        result
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TennisCourtController.class))
                .andExpect(handler().methodName("createTennisCourt"))
                .andExpect(jsonPath("$.code").value("200-3"))
                .andExpect(jsonPath("$.message").value("양평누리 테니스장이 등록되었습니다."))
                .andExpect(jsonPath("$.data.name").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.location").value("서울 영등포구"));

    }

    @Test
    @DisplayName("테니스장 등록 실패 - 필수 입력값 누락")
    void create2() throws Exception {
        // given
        String name = "";
        String location = "";
        String imageUrl = "";

        // when
        ResultActions result = createTennisCourtRequest(name, location, imageUrl);

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("테니스장 이름은 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("테니스장 위치는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("이미지 URL은 필수 입력값입니다.")));
    }

    @Test
    @DisplayName("테니스장 등록 실패 - 중복 테니스장 이름")
    void create3() throws Exception {
        // given
        String name = "양평누리 테니스장";
        String location = "서울 영등포구";
        String imageUrl = "http://image.url";
        createTennisCourtRequest(name, location, imageUrl);

        // when
        // given
        String name2 = "양평누리 테니스장";
        String location2 = "서울 강남구";
        String imageUrl2 = "http://test.url";
        ResultActions result = createTennisCourtRequest(name2, location2, imageUrl2);


        // then
        result
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-6"))
                .andExpect(jsonPath("$.message").value("이미 같은 이름의 테니스장이 존재합니다."));
    }
}