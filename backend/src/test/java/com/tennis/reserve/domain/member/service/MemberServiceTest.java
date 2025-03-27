package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.global.BaseTestConfig;
import com.tennis.reserve.global.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@BaseTestConfig
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRedisService memberRedisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void join1() {
        // given : JoinReqForm 이 주어지고
        JoinReqForm form = new JoinReqForm(
                "testUser",
                "!password1",
                "nickname",
                "test@example.com"
        );

        // when : 서비스의 createMember를 통해 회원가입을 성공하면
        MemberResBody result = memberService.createMember(form);

        // then : 받은 dto를 통해 assertThat 검증
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getNickname()).isEqualTo("nickname");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("회원가입 실패 - 중복")
    void join2() {
        // given : Member를 레포지토리에 저장
        JoinReqForm existing = new JoinReqForm(
                "testUser",
                "!password1",
                "nickname",
                "test@example.com"
        );
        memberService.createMember(existing);

        // when : 같은 아이디로 회원가입
        JoinReqForm form = new JoinReqForm(
                "testUser",
                "!password1",
                "nickname1",
                "test1@example.com"
        );


        // then : 받은 dto를 통해 assertThat 검증
        assertThatThrownBy(() -> memberService.createMember(form))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login1() {
        // given
        String rawPassword = "!password1";
        JoinReqForm existing = new JoinReqForm(
                "testUser",
                "!password1",
                "nickname",
                "test@example.com"
        );
        memberService.createMember(existing);

        LoginReqForm form = new LoginReqForm("testUser", rawPassword);

        // when
        LoginResBody result = memberService.loginMember(form);

        // then
        assertThat(result.getAccessToken()).isNotBlank();
        assertThat(result.getItems().getUsername()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 없음")
    void login2() {
        // given
        LoginReqForm form = new LoginReqForm("noUser", "!password1");

        // when, then
        assertThatThrownBy(() -> memberService.loginMember(form))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void login3() {
        // given
        JoinReqForm existing = new JoinReqForm(
                "testUser",
                "!password1",
                "nickname",
                "test@example.com"
        );
        memberService.createMember(existing);

        // when
        LoginReqForm form = new LoginReqForm("testUser", "wrong");

        // then
        assertThatThrownBy(() -> memberService.loginMember(form))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("비밀번호를 올바르게 입력해주세요.");
    }
}