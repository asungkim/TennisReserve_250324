package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.AuthToken;
import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.global.exception.ServiceException;
import com.tennis.reserve.global.standard.util.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRedisService memberRedisService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    public MemberResBody createMember(JoinReqForm body) {
        // 1. username, nickname, email 중복 체크
        validateDuplicateMember(body.username(), body.nickname(), body.email());

        // 2. 문제 없으면 생성
        Member member = Member.builder()
                .username(body.username())
                .password(passwordEncoder.encode(body.password()))
                .nickname(body.nickname())
                .email(body.email())
                .build();


        // 3. 레포지토리에 저장 및 DTO 반환
        memberRepository.save(member);
        return MemberResBody.fromEntity(member);
    }

    private void validateDuplicateMember(String username, String nickname, String email) {
        if (memberRepository.existsByUsername(username)) {
            throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new ServiceException("409-2", "이미 존재하는 닉네임입니다.");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new ServiceException("409-3", "이미 존재하는 이메일입니다.");
        }
    }


    public LoginResBody loginMember(LoginReqForm loginReqForm) {
        // 1. 로그인 입력 폼 검증 후 멤버 리턴
        Member member = validateLoginForm(loginReqForm.username(), loginReqForm.password());

        // 2. 토큰 발급
        AuthToken authToken = authTokenService.generateAuthToken(member);

        // 3. accessToken 쿠키에 저장, refreshToken redis에 저장
        String accessToken = authToken.accessToken();
        String refreshToken = authToken.refreshToken();

        rq.addCookie("accessToken", accessToken, 60 * 60);
        memberRedisService.save(member, refreshToken);

        // 4. 정보 담아서 리턴
        return LoginResBody.builder()
                .item(MemberResBody.fromEntity(member))
                .accessToken(accessToken)
                .build();
    }

    private Member validateLoginForm(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException("409-4", "아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ServiceException("409-5", "비밀번호를 올바르게 입력해주세요.");
        }

        return member;
    }
}
