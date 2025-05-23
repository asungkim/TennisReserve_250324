package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.domain.tennis.dto.request.CourtReqForm;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
import com.tennis.reserve.domain.tennis.dto.response.court.CourtResponse;
import com.tennis.reserve.domain.tennis.service.CourtService;
import com.tennis.reserve.domain.tennis.service.TennisCourtService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class CourtControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TennisCourtService tennisCourtService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;


    @Autowired
    private CourtService courtService;

    private Long tennisCourtId;

    private String adminAccessToken;
    private String userAccessToken;

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


        tennisCourtId = tennisCourtService.createTennisCourt(new TennisCourtReqForm(
                "양평누리 테니스장", "서울시 영등포구", "http://test1.url"
        )).id();
    }

    private ResultActions createCourtRequest(String courtCode, String surfaceType, String environment, String token) throws Exception {
        return mvc.perform(post("/api/tennis-courts/%d/courts".formatted(tennisCourtId))
                        .content("""
                                {
                                    "courtCode": "%s",
                                    "surfaceType": "%s",
                                    "environment": "%s"
                                }
                                """.formatted(courtCode, surfaceType, environment).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print());
    }

    @Test
    @DisplayName("코트 등록 성공")
    void create1() throws Exception {
        // when
        ResultActions result = createCourtRequest("A", "GRASS", "OUTDOOR", adminAccessToken);


        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-3"))
                .andExpect(jsonPath("$.data.tennisCourtName").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.courtCode").value("A"));

    }

    @Test
    @DisplayName("코트 등록 실패 - 해당 테니스장에 이미 있는 코트 코드")
    void create2() throws Exception {
        // given
        createCourtRequest("A", "HARD", "INDOOR", adminAccessToken);

        // when 이미 A 코드가 있으면
        ResultActions result = createCourtRequest("A", "GRASS", "OUTDOOR", adminAccessToken);


        // then
        result
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-1"))
                .andExpect(jsonPath("$.message").value("해당 테니스장에는 이미 A 코트가 존재합니다."));

    }

    @Test
    @DisplayName("코트 등록 실패 - 필수 입력값 누락")
    void create3() throws Exception {
        // given,when
        ResultActions result = mvc.perform(post("/api/tennis-courts/%d/courts".formatted(tennisCourtId))
                        .content("""
                                {
                                    "courtCode": "",
                                    "surfaceType": "",
                                    "environment": ""
                                }
                                """
                                .stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + adminAccessToken))

                .andDo(print());

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("코트 코드는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("표면 종류(SurfaceType)는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("환경(Environment)은 필수 입력값입니다.")));


    }


    @Test
    @DisplayName("코트 목록 조회 성공 - 유저 조회")
    void list1() throws Exception {
        // given
        createCourtRequest("A", "GRASS", "OUTDOOR", adminAccessToken);
        createCourtRequest("B", "HARD", "INDOOR", adminAccessToken);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts", tennisCourtId)
                        .header("Authorization", "Bearer " + userAccessToken)
                )
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-4"))
                .andExpect(jsonPath("$.message").value(containsString("양평누리 테니스장의 코트 목록을 조회하였습니다.")))
                .andExpect(jsonPath("$.data.tennisCourtId").value(tennisCourtId))
                .andExpect(jsonPath("$.data.tennisCourtName").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.courtList").isArray())
                .andExpect(jsonPath("$.data.courtList.length()").value(2))
                .andExpect(jsonPath("$.data.courtList[0].courtCode").value("A"))
                .andExpect(jsonPath("$.data.courtList[1].courtCode").value("B"));
    }

    @Test
    @DisplayName("코트 목록 조회 실패 - 존재하지 않는 테니스장 ID")
    void list2() throws Exception {
        // given
        Long invalidTennisCourtId = 9999L;

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts", invalidTennisCourtId)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 테니스장입니다."));
    }

    @Test
    @DisplayName("코트 단건 조회 성공")
    void get1() throws Exception {
        // given
        CourtResponse savedCourt = courtService.createCourt(
                new CourtReqForm("A", "HARD", "OUTDOOR"),
                tennisCourtId
        );

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{id}",
                        tennisCourtId, savedCourt.id())
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("양평누리 테니스장 의 A 코트를 조회하였습니다.")))
                .andExpect(jsonPath("$.code").value("200-5"))
                .andExpect(jsonPath("$.data.courtCode").value("A"))
                .andExpect(jsonPath("$.data.tennisCourtId").value(tennisCourtId));
    }

    @Test
    @DisplayName("코트 단건 조회 실패 - 존재하지 않는 테니스장 or 코트 ID")
    void get2() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{id}",
                        999L, 999L)
                        .header("Authorization", "Bearer " + userAccessToken))
                .andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("해당 테니스장 또는 코트를 찾을 수 없습니다.")));
    }


    @Test
    @DisplayName("코트 수정 성공")
    void modify1() throws Exception {
        // given
        CourtResponse savedCourt = courtService.createCourt(
                new CourtReqForm("A", "HARD", "OUTDOOR"),
                tennisCourtId
        );

        // when
        ResultActions result = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{id}", tennisCourtId, savedCourt.id())
                .content("""
                        {
                          "courtCode": "B",
                          "surfaceType": "CLAY",
                          "environment": "INDOOR"
                        }
                        """.stripIndent())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courtCode").value("B"))
                .andExpect(jsonPath("$.data.surfaceType").value("CLAY"))
                .andExpect(jsonPath("$.data.environment").value("INDOOR"));
    }

    @Test
    @DisplayName("코트 수정 실패 - 유효성 검증 실패")
    void modify2() throws Exception {
        // when
        ResultActions result = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{id}", tennisCourtId, 1L)
                .content("""
                        {
                          "courtCode": "",
                          "surfaceType": "",
                          "environment": ""
                        }
                        """.stripIndent())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("코트 코드는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("표면 종류(SurfaceType)는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("환경(Environment)은 필수 입력값입니다.")));
    }

    @Test
    @DisplayName("코트 수정 실패 - 존재하지 않는 테니스장 ID")
    void modify3() throws Exception {
        // when
        ResultActions result = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{id}", 999L, 1L)
                .content("""
                        {
                          "courtCode": "B",
                          "surfaceType": "HARD",
                          "environment": "OUTDOOR"
                        }
                        """.stripIndent())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("해당 테니스장 또는 코트를 찾을 수 없습니다.")));
    }

    @Test
    @DisplayName("코트 수정 실패 - 존재하지 않는 코트 ID")
    void modify4() throws Exception {
        // when
        ResultActions result = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{id}", tennisCourtId, 9999L)
                .content("""
                        {
                          "courtCode": "C",
                          "surfaceType": "CLAY",
                          "environment": "INDOOR"
                        }
                        """.stripIndent())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("해당 테니스장 또는 코트를 찾을 수 없습니다.")));
    }

    @Test
    @DisplayName("코트 삭제 성공 - 어드민")
    void delete1() throws Exception {
        // given
        CourtResponse savedCourt = courtService.createCourt(
                new CourtReqForm("A", "HARD", "OUTDOOR"),
                tennisCourtId
        );

        // when
        ResultActions result = mvc.perform(delete("/api/tennis-courts/{tennisCourtId}/courts/{id}",
                        tennisCourtId, savedCourt.id())
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-7"))
                .andExpect(jsonPath("$.message").value(containsString("양평누리 테니스장 의 A 코트를 삭제하였습니다.")));
    }

    @Test
    @DisplayName("코트 삭제 실패 - 존재하지 않는 테니스장 or 코트 ID")
    void delete2() throws Exception {
        // when
        ResultActions result = mvc.perform(delete("/api/tennis-courts/{tennisCourtId}/courts/{id}",
                        999L, 999L)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("해당 테니스장 또는 코트를 찾을 수 없습니다.")));
    }

}