package com.tennis.reserve.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennis.reserve.domain.member.service.MemberService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

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

        ResultActions resultActions = joinRequest(username,password,nickname,email);

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

        ResultActions resultActions = joinRequest(username,password,nickname,email);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("createMember"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));

        ResultActions same = joinRequest(username,password,nickname,email);
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

        ResultActions resultActions = joinRequest(username1,password1,nickname1,email1);

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

        ResultActions sameNickname = joinRequest(username2,password2,nickname2,email2);
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

        ResultActions resultActions = joinRequest(username1,password1,nickname1,email1);

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

        ResultActions sameNickname = joinRequest(username2,password2,nickname2,email2);
        sameNickname
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409-3"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));

    }


}