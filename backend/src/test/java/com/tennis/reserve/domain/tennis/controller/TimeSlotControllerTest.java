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
import com.tennis.reserve.domain.tennis.dto.response.CourtResponse;
import com.tennis.reserve.domain.tennis.dto.response.TennisCourtResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CourtService courtService;

    @Autowired
    private TennisCourtService tennisCourtService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    private Long tennisCourtId;
    private Long courtId;

    private String userAccessToken;
    private String adminAccessToken;

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

        TennisCourtResponse tennisCourtResponse = tennisCourtService.createTennisCourt(new TennisCourtReqForm("서초구 테니스장", "서울 서초구", "http://image.url"));
        tennisCourtId = tennisCourtResponse.id();

        CourtResponse courtResponse = courtService.createCourt(new CourtReqForm("A코트", "HARD", "OUTDOOR"), tennisCourtResponse.id());
        courtId = courtResponse.id();
    }

    private ResultActions createTimeSlotRequest(String startTime, String endTime, String token) throws Exception {
        return mvc.perform(post("/api/tennis-courts/%d/courts/%d/time-slots".formatted(tennisCourtId, courtId))
                        .content("""
                                    {
                                        "startTime": "%s",
                                        "endTime": "%s"
                                    }
                                """.formatted(startTime, endTime).stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .header("Authorization", "Bearer " + token)
                )
                .andDo(print());
    }

    @Test
    @DisplayName("시간대 등록 성공")
    void create1() throws Exception {
        String start = "2025-04-01T10:00:00";
        String end = "2025-04-01T12:00:00";

        ResultActions result = createTimeSlotRequest(start, end, adminAccessToken);

        result
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TimeSlotController.class))
                .andExpect(handler().methodName("createTimeSlot"))
                .andExpect(jsonPath("$.code").value("200-3"))
                .andExpect(jsonPath("$.message").value("시간대가 등록되었습니다."))
                .andExpect(jsonPath("$.data.startTime").value(start))
                .andExpect(jsonPath("$.data.endTime").value(end));
    }

    @Test
    @DisplayName("시간대 등록 실패 - 필수 입력값 누락")
    void create2() throws Exception {
        String requestJson = """
                    {
                    }
                """;

        ResultActions result = mvc.perform(post("/api/tennis-courts/%d/courts/%d/time-slots".formatted(tennisCourtId, courtId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print());

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.message", containsString("시작 시간은 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("종료 시간은 필수 입력값입니다.")));
    }

    @Test
    @DisplayName("시간대 등록 실패 - 중복 시간대")
    void create3() throws Exception {
        // given
        String start = "2025-04-01T10:00:00";
        String end = "2025-04-01T12:00:00";
        createTimeSlotRequest(start, end, adminAccessToken);

        // 동일 시간대 다시 등록
        String start2 = "2025-04-01T11:00:00";
        ResultActions result = createTimeSlotRequest(start2, end, adminAccessToken);

        result
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-3"))
                .andExpect(jsonPath("$.message").value("겹치는 시간대가 이미 생성되어 있습니다."));
    }

}