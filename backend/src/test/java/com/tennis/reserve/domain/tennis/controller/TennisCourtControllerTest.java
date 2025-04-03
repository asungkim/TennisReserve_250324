package com.tennis.reserve.domain.tennis.controller;

import com.jayway.jsonpath.JsonPath;
import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    private String userAccessToken;

    private String adminAccessToken;
    @Autowired
    private MemberRepository memberRepository;

    private ResultActions createTennisCourtRequest(String name, String location, String imageUrl, String token) throws Exception {
        return mvc.perform(post("/api/tennis-courts")
                        .content("""
                                {
                                    "name": "%s",
                                    "location": "%s",
                                    "imageUrl": "%s"
                                }
                                """.formatted(name, location, imageUrl).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print());
    }

    @BeforeEach
    void setUp() {
        // 회원가입 후 로그인(일반 회원)
        MemberResBody member = memberService.createMember(new JoinReqForm("user1", "!password1", "userNick", "user1@exam.com"));
        LoginResBody loginResBody = memberService.loginMember(new LoginReqForm("user1", "!password1"));
        userAccessToken = loginResBody.accessToken();

        // 회원가입 후 로그인(어드민)
        Member admin = Member.createAdmin();
        admin.encodePassword(passwordEncoder);
        memberRepository.save(admin);
        LoginResBody adminLogin = memberService.loginMember(new LoginReqForm("admin", "!password1"));
        adminAccessToken = adminLogin.accessToken();
    }

    @Test
    @DisplayName("테니스장 등록 성공 - 관리자가 생성")
    void create1() throws Exception {
        // given
        String name = "양평누리 테니스장";
        String location = "서울 영등포구";
        String imageUrl = "http://image.url";

        // when
        ResultActions result = createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

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
        ResultActions result = createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

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
        createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

        // when
        // given
        String name2 = "양평누리 테니스장";
        String location2 = "서울 강남구";
        String imageUrl2 = "http://test.url";
        ResultActions result = createTennisCourtRequest(name2, location2, imageUrl2, adminAccessToken);


        // then
        result
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-6"))
                .andExpect(jsonPath("$.message").value("이미 같은 이름의 테니스장이 존재합니다."));
    }

    @Test
    @DisplayName("테니스장 목록 조회 성공 - 유저가 조회")
    void getList() throws Exception {
        // given
        String name = "잠실올림픽코트";
        String location = "서울 송파구";
        String imageUrl = "http://image1.url";
        createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

        String name2 = "양재한강코트";
        String location2 = "서울 강남구";
        String imageUrl2 = "http://image2.url";
        createTennisCourtRequest(name2, location2, imageUrl2, adminAccessToken);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(TennisCourtController.class))
                .andExpect(handler().methodName("getTennisCourts"))
                .andExpect(jsonPath("$.code").value("200-4"))
                .andExpect(jsonPath("$.message").value("테니스장 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[1].name").value(name2));
    }

    @Test
    @DisplayName("테니스장 단건 조회 성공 - 유저가 조회")
    void getTennisCourt1() throws Exception {
        // given
        String name = "잠실올림픽코트";
        String location = "서울 송파구";
        String imageUrl = "http://image1.url";
        createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

        String name2 = "양재한강코트";
        String location2 = "서울 강남구";
        String imageUrl2 = "http://image2.url";
        ResultActions createResult = createTennisCourtRequest(name2, location2, imageUrl2, adminAccessToken);
        String response = createResult.andReturn().getResponse().getContentAsString();
        Long courtId = ((Number) JsonPath.read(response, "$.data.id")).longValue();

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{id}", courtId)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(TennisCourtController.class))
                .andExpect(handler().methodName("getTennisCourt"))
                .andExpect(jsonPath("$.code").value("200-5"))
                .andExpect(jsonPath("$.message").value(name2 + " 조회하였습니다."))
                .andExpect(jsonPath("$.data.name").value(name2))
                .andExpect(jsonPath("$.data.location").value(location2))
                .andExpect(jsonPath("$.data.imageUrl").value(imageUrl2));
    }


    @Test
    @DisplayName("테니스장 단건 조회 실패 - 없는 데이터")
    void getTennisCourt2() throws Exception {
        // given
        String name = "잠실올림픽코트";
        String location = "서울 송파구";
        String imageUrl = "http://image1.url";
        createTennisCourtRequest(name, location, imageUrl, adminAccessToken);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{id}", 2L)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 테니스장입니다."));
    }

    private ResultActions modifyRequest(Long courtId, String name, String location, String imageUrl, String token) throws Exception {
        return mvc.perform(put("/api/tennis-courts/{id}", courtId)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {
                                    "name": "%s",
                                    "location": "%s",
                                    "imageUrl": "%s"
                                }
                                """.formatted(name, location, imageUrl).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)))
                .andDo(print());
    }

    @Test
    @DisplayName("테니스장 수정 성공 - 관리자가 수정")
    void modify1() throws Exception {
        // given
        String name = "초기 테니스장";
        String location = "서울시 구로구";
        String imageUrl = "http://original.image";
        ResultActions prevResult = createTennisCourtRequest(name, location, imageUrl, adminAccessToken);
        String response = prevResult.andReturn().getResponse().getContentAsString();
        Long courtId = ((Number) JsonPath.read(response, "$.data.id")).longValue();

        // when
        String newName = "수정된 테니스장";
        String newLocation = "서울시 강남구";
        String newImageUrl = "http://updated.image";

        ResultActions result = modifyRequest(courtId, newName, newLocation, newImageUrl, adminAccessToken);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-6"))
                .andExpect(jsonPath("$.message").value(newName + " 수정하였습니다."))
                .andExpect(jsonPath("$.data.name").value(newName))
                .andExpect(jsonPath("$.data.location").value(newLocation))
                .andExpect(jsonPath("$.data.imageUrl").value(newImageUrl));
    }

    @Test
    @DisplayName("테니스장 수정 실패 - 필수 입력값 공백")
    void modify2() throws Exception {
        // given
        String name = "초기 테니스장";
        String location = "서울시 구로구";
        String imageUrl = "http://original.image";
        ResultActions tennisCourtRequest = createTennisCourtRequest(name, location, imageUrl, adminAccessToken);
        String response = tennisCourtRequest.andReturn().getResponse().getContentAsString();
        Long courtId = ((Number) JsonPath.read(response, "$.data.id")).longValue();

        // when
        ResultActions result = modifyRequest(courtId, "", "", "", adminAccessToken);

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.message", containsString("테니스장 이름은 공백일 수 없습니다.")))
                .andExpect(jsonPath("$.message", containsString("위치는 공백일 수 없습니다.")))
                .andExpect(jsonPath("$.message", containsString("이미지 URL은 공백일 수 없습니다.")));
    }
}