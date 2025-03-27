package com.tennis.reserve.domain.member.controller;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.domain.member.service.AuthTokenService;
import com.tennis.reserve.domain.member.service.MemberRedisService;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.global.BaseTestConfig;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@BaseTestConfig
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private MemberRedisService memberRedisService;

    private ResultActions joinRequest(String username, String password, String nickname, String email) throws Exception {
        return mvc
                .perform(
                        post("/api/members/join")
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s",
                                            "nickname": "%s",
                                            "email": "%s"
                                        }
                                        """
                                        .formatted(username, password, nickname, email)
                                        .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void join1() throws Exception {
        String username = "user1";
        String password = "!password1";
        String nickname = "userOne";
        String email = "user1@exam.com";

        ResultActions resultActions = joinRequest(username, password, nickname, email);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));
    }

    @Test
    @DisplayName("회원가입 - 중복 아이디")
    void join2() throws Exception {
        String username = "user1";
        String password = "!password1";
        String nickname = "userOne";
        String email = "user1@exam.com";

        ResultActions resultActions = joinRequest(username, password, nickname, email);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));

        ResultActions same = joinRequest(username, password, nickname, email);
        same
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-1"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디입니다."));

    }

    @Test
    @DisplayName("회원가입 - 중복 닉네임")
    void join3() throws Exception {
        String username1 = "user1";
        String password1 = "!password1";
        String nickname1 = "userOne";
        String email1 = "user1@exam.com";

        ResultActions resultActions = joinRequest(username1, password1, nickname1, email1);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));

        String username2 = "user2";
        String password2 = "!password1";
        String nickname2 = "userOne";
        String email2 = "user2@exam.com";

        ResultActions sameNickname = joinRequest(username2, password2, nickname2, email2);
        sameNickname
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-2"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 닉네임입니다."));

    }

    @Test
    @DisplayName("회원가입 - 중복 이메일")
    void join4() throws Exception {
        String username1 = "user1";
        String password1 = "!password1";
        String nickname1 = "userOne";
        String email1 = "user1@exam.com";

        ResultActions resultActions = joinRequest(username1, password1, nickname1, email1);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));

        String username2 = "user2";
        String password2 = "!password1";
        String nickname2 = "userTwo";
        String email2 = "user1@exam.com";

        ResultActions sameNickname = joinRequest(username2, password2, nickname2, email2);
        sameNickname
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-3"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));

    }

    @Test
    @DisplayName("회원가입 - 필수 입력 데이터 누락")
    void join5() throws Exception {
        String username1 = "";
        String password1 = "";
        String nickname1 = "";
        String email1 = "";

        ResultActions resultActions = joinRequest(username1, password1, nickname1, email1);

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.message", containsString("닉네임은 2~20자 사이여야 합니다.")))
                .andExpect(jsonPath("$.message", containsString("비밀번호는 8~50자 사이여야 합니다.")))
                .andExpect(jsonPath("$.message", containsString("비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.")))
                .andExpect(jsonPath("$.message", containsString("아이디는 4~20자 사이여야 합니다.")))
                .andExpect(jsonPath("$.message", containsString("아이디는 영문과 숫자만 사용할 수 있습니다.")))
                .andExpect(jsonPath("$.message", containsString("이메일은 필수 입력값입니다.")));


    }

    private ResultActions loginRequest(String username, String password) throws Exception {
        return mvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username, password).stripIndent())
                )
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login1() throws Exception {
        // 먼저 회원가입 (DB에 사용자 등록)
        joinRequest("user1", "!password1", "nickname1", "user1@exam.com");

        // 로그인 요청
        ResultActions result = loginRequest("user1", "!password1");

        // LoginResBody 검증
        result.andExpect(status().isOk())
                .andExpect(handler().methodName("loginMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-2"))
                .andExpect(jsonPath("$.message").value("로그인에 성공하였습니다."))
                .andExpect(jsonPath("$.data.items.username").value("user1"))
                .andExpect(jsonPath("$.data.items.nickname").value("nickname1"))
                .andExpect(jsonPath("$.data.items.email").value("user1@exam.com"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(cookie().exists("accessToken"));

        // accessToken 파싱
        String accessToken = result.andReturn().getResponse().getCookie("accessToken").getValue();
        Map<String, Object> payload = authTokenService.getPayload(accessToken);
        assertThat(payload.get("username")).isEqualTo("user1");
        assertThat(payload.get("nickname")).isEqualTo("nickname1");

        // redis 저장 확인
        Long memberId = Long.valueOf(payload.get("id").toString());
        Member member = memberRepository.findById(memberId).orElseThrow();
        Optional<String> refreshTokenOpt = memberRedisService.get(member);
        assertThat(refreshTokenOpt).isPresent(); // refreshToken 저장됨
    }

    @Test
    @DisplayName("로그인 실패 - 없는 아이디")
    void login2() throws Exception {
        // 먼저 회원가입 (DB에 사용자 등록)
        joinRequest("user1", "!password1", "nickname1", "user1@exam.com");

        // 로그인 요청
        ResultActions result = loginRequest("noUser", "!password1");

        // LoginResBody 검증
        result.andExpect(status().isConflict())
                .andExpect(handler().methodName("loginMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("409-4"))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));

        /*
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ServiceException("409-5", "비밀번호를 올바르게 입력해주세요.");
        }
        * */
    }

    @Test
    @DisplayName("로그인 실패 - 틀린 비밀번호")
    void login3() throws Exception {
        // 먼저 회원가입 (DB에 사용자 등록)
        joinRequest("user1", "!password1", "nickname1", "user1@exam.com");

        // 로그인 요청
        ResultActions result = loginRequest("user1", "wrong");

        // LoginResBody 검증
        result.andExpect(status().isConflict())
                .andExpect(handler().methodName("loginMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("409-5"))
                .andExpect(jsonPath("$.message").value("비밀번호를 올바르게 입력해주세요."));
    }

    @Test
    @DisplayName("로그인 실패 - Valid 검증")
    void login4() throws Exception {
        // 먼저 회원가입 (DB에 사용자 등록)
        joinRequest("user1", "!password1", "nickname1", "user1@exam.com");

        // 로그인 요청
        String loginUsername = "";
        String loginPassword = "";
        ResultActions result = loginRequest(loginUsername, loginPassword);

        // LoginResBody 검증
        result.andExpect(status().isBadRequest())
                .andExpect(handler().methodName("loginMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.message", containsString("아이디는 필수 입력값입니다.")))
                .andExpect(jsonPath("$.message", containsString("비밀번호는 필수 입력값입니다.")));
    }

}