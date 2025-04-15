package com.tennis.reserve.domain.tennis.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.tennis.reserve.domain.tennis.dto.response.tennisCourt.TennisCourtResponse;
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
import org.springframework.test.web.servlet.MvcResult;
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
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;


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

        TennisCourtResponse tennisCourtResponse = tennisCourtService.createTennisCourt(new TennisCourtReqForm("양평누리 테니스장", "서울 영등포구", "http://image.url"));
        tennisCourtId = tennisCourtResponse.id();

        CourtResponse courtResponse = courtService.createCourt(new CourtReqForm("A", "HARD", "OUTDOOR"), tennisCourtResponse.id());
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

    private Long extractTimeSlotId(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(content);
        return root.path("data").path("id").asLong();
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
                .andExpect(jsonPath("$.message").value("양평누리 테니스장 의 A 코트에 10:00 ~ 12:00 시간대가 등록되었습니다."))
                .andExpect(jsonPath("$.data.tennisCourtName").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.courtCode").value("A"))
                .andExpect(jsonPath("$.data.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("12:00:00"));
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

    @Test
    @DisplayName("시간대 목록 조회 성공 - 유저")
    void list1() throws Exception {
        // given - 시간대 하나 등록
        String start1 = "10:00:00";
        String end1 = "12:00:00";
        createTimeSlotRequest(start1, end1, adminAccessToken);

        String start2 = "12:00:00";
        String end2 = "14:00:00";
        createTimeSlotRequest(start2, end2, adminAccessToken);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots", tennisCourtId, courtId)
                        .header("Authorization", "Bearer " + userAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TimeSlotController.class))
                .andExpect(handler().methodName("getTimeSlotList"))
                .andExpect(jsonPath("$.code").value("200-4"))
                .andExpect(jsonPath("$.message").value("양평누리 테니스장 의 A 코트에 시간대 목록을 조회하였습니다."))
                .andExpect(jsonPath("$.data.tennisCourtId").value(tennisCourtId))
                .andExpect(jsonPath("$.data.courtId").value(courtId))
                .andExpect(jsonPath("$.data.tennisCourtName").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.courtCode").value("A"))
                .andExpect(jsonPath("$.data.timeSlots.length()").value(2))
                .andExpect(jsonPath("$.data.timeSlots[0].startTime").value("10:00:00"))
                .andExpect(jsonPath("$.data.timeSlots[0].endTime").value("12:00:00"))
                .andExpect(jsonPath("$.data.timeSlots[1].startTime").value("12:00:00"))
                .andExpect(jsonPath("$.data.timeSlots[1].endTime").value("14:00:00"));
    }

    @Test
    @DisplayName("시간대 목록 조회 실패 - 존재하지 않는 테니스장 or 코트 ID")
    void list2() throws Exception {
        // given - 시간대 하나 등록
        String start1 = "10:00:00";
        String end1 = "12:00:00";
        createTimeSlotRequest(start1, end1, adminAccessToken);

        String start2 = "12:00:00";
        String end2 = "14:00:00";
        createTimeSlotRequest(start2, end2, adminAccessToken);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots", 999L, 999L)
                        .header("Authorization", "Bearer " + userAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.message").value("해당 테니스장 또는 코트를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("시간대 단건 조회 성공 - 유저")
    void getOne1() throws Exception {
        // given
        String start = "10:00:00";
        String end = "12:00:00";
        MvcResult timeSlotRequest = createTimeSlotRequest(start, end, adminAccessToken).andReturn();

        Long timeSlotId = extractTimeSlotId(timeSlotRequest);

        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                        tennisCourtId, courtId, timeSlotId)
                        .header("Authorization", "Bearer " + userAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(TimeSlotController.class))
                .andExpect(handler().methodName("getTimeSlot"))
                .andExpect(jsonPath("$.code").value("200-5"))
                .andExpect(jsonPath("$.message").value("양평누리 테니스장 의 A 코트에 10:00 ~ 12:00 시간대를 조회하였습니다."))
                .andExpect(jsonPath("$.data.tennisCourtName").value("양평누리 테니스장"))
                .andExpect(jsonPath("$.data.courtCode").value("A"))
                .andExpect(jsonPath("$.data.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("12:00:00"));
    }

    @Test
    @DisplayName("시간대 단건 조회 실패 - 존재하지 않는 시간대 ID")
    void getOne2() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                        tennisCourtId, courtId, 9999L)
                        .header("Authorization", "Bearer " + userAccessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404-2"))
                .andExpect(jsonPath("$.message").value("해당 시간대를 찾을 수 없습니다."));
    }




    @Test
    @DisplayName("시간대 수정 성공 - status 포함")
    void modify1() throws Exception {
        // given: 시간대 등록
        String start = "10:00:00";
        String end = "12:00:00";

        MvcResult result = createTimeSlotRequest(start, end, adminAccessToken)
                .andExpect(status().isOk())
                .andReturn();

        Long timeSlotId = extractTimeSlotId(result);

        // when: 시간대 수정 요청
        ResultActions modifyResult = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                        tennisCourtId, courtId, timeSlotId)
                        .content("""
                                {
                                    "startTime": "13:00:00",
                                    "endTime": "15:00:00",
                                    "status": "RESERVED"
                                }
                                """.stripIndent())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print());

        // then
        modifyResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-6"))
                .andExpect(jsonPath("$.message").value("양평누리 테니스장 의 A 코트의 특정 시간대를 13:00 ~ 15:00 로 수정하였습니다."))
                .andExpect(jsonPath("$.data.startTime").value("13:00:00"))
                .andExpect(jsonPath("$.data.endTime").value("15:00:00"))
                .andExpect(jsonPath("$.data.status").value("RESERVED"));
    }

    @Test
    @DisplayName("시간대 수정 성공 - status 없이")
    void modify2() throws Exception {
        // given - 기존 시간대 등록
        String start = "10:00:00";
        String end = "12:00:00";
        MvcResult result = createTimeSlotRequest(start, end, adminAccessToken).andReturn();

        // 등록된 ID 가져오기
        Long timeSlotId = extractTimeSlotId(result);

        // when - status 없이 수정 요청
        String newStart = "14:00:00";
        String newEnd = "16:00:00";

        ResultActions modifyResult = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                tennisCourtId, courtId, timeSlotId)
                .content("""
                        {
                          "startTime": "%s",
                          "endTime": "%s"
                        }
                        """.formatted(newStart, newEnd).stripIndent())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        modifyResult
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-6"))
                .andExpect(jsonPath("$.data.startTime").value(newStart))
                .andExpect(jsonPath("$.data.endTime").value(newEnd))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE")); // 기존 status 유지
    }

    @Test
    @DisplayName("시간대 수정 실패 - 존재하지 않는 시간대 ID")
    void modify3() throws Exception {
        // given
        String requestJson = """
                    {
                        "startTime": "13:00:00",
                        "endTime": "15:00:00",
                        "status": "AVAILABLE"
                    }
                """;

        // when
        ResultActions result = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                tennisCourtId, courtId, 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404-2"))
                .andExpect(jsonPath("$.message").value("해당 시간대를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("시간대 수정 실패 - 유효하지 않은 status 값")
    void modify4() throws Exception {
        // given - 등록
        String start = "10:00:00";
        String end = "12:00:00";
        MvcResult result = createTimeSlotRequest(start, end, adminAccessToken).andReturn();
        Long timeSlotId = extractTimeSlotId(result); // 이건 아래에 helper 함수로 따로 설명

        String invalidRequestJson = """
                    {
                        "startTime": "13:00:00",
                        "endTime": "15:00:00",
                        "status": "WRONG_STATUS"
                    }
                """;

        // when
        ResultActions res = mvc.perform(put("/api/tennis-courts/{tennisCourtId}/courts/{courtId}/time-slots/{id}",
                tennisCourtId, courtId, timeSlotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson)
                .header("Authorization", "Bearer " + adminAccessToken)
        ).andDo(print());

        // then
        res
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400-2"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 TimeStatus입니다. (입력값: WRONG_STATUS)"));
    }


}