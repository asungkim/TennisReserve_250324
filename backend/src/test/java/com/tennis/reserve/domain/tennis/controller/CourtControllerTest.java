package com.tennis.reserve.domain.tennis.controller;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.domain.tennis.dto.request.TennisCourtReqForm;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private ResultActions createCourtRequest(String courtCode, String surfaceType, String environment, Long tennisCourtId, String token) throws Exception {
        return mvc.perform(post("/api/courts")
                        .content("""
                                {
                                    "courtCode": "%s",
                                    "surfaceType": "%s",
                                    "environment": "%s",
                                    "tennisCourtId": %d
                                }
                                """.formatted(courtCode, surfaceType, environment, tennisCourtId).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print());
    }

    @Test
    @DisplayName("코트 등록 성공")
    void create1() throws Exception {
        // when
        ResultActions result = createCourtRequest("A", "GRASS", "OUTDOOR", tennisCourtId, adminAccessToken);


        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-3"))
                .andExpect(jsonPath("$.message").value("코트가 등록되었습니다"))
                .andExpect(jsonPath("$.data.courtCode").value("A"));

    }

    @Test
    @DisplayName("코트 등록 실패 - 해당 테니스장에 이미 있는 코트 코드")
    void create2() throws Exception {
        // given
        createCourtRequest("A", "HARD", "INDOOR", tennisCourtId, adminAccessToken);

        // when 이미 A 코드가 있으면
        ResultActions result = createCourtRequest("A", "GRASS", "OUTDOOR", tennisCourtId, adminAccessToken);


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
        ResultActions result = mvc.perform(post("/api/courts")
                        .content("""
                                {
                                    "courtCode": ""
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
                .andExpect(jsonPath("$.message", containsString("환경(Environment)은 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("테니스장 ID는 필수 입력값입니다.")));

    }

}