package com.tennis.reserve.domain.member.controller;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.request.LoginReqForm;
import com.tennis.reserve.domain.member.dto.response.LoginResBody;
import com.tennis.reserve.domain.member.dto.response.MemberResBody;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.service.MemberAuthService;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.global.dto.Empty;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;

    // TODO: 로그아웃, 내 정보 수정, 내 정보 조회

    @PostMapping("/join")
    public RsData<MemberResBody> createMember(@RequestBody @Valid JoinReqForm joinReqForm) {
        MemberResBody memberResBody = memberService.createMember(joinReqForm);

        return new RsData<>(
                "200-1",
                "회원가입에 성공하였습니다.",
                memberResBody
        );
    }

    @PostMapping("/login")
    public RsData<LoginResBody> loginMember(@RequestBody @Valid LoginReqForm loginReqForm) {
        LoginResBody loginResBody = memberService.loginMember(loginReqForm);

        return new RsData<>(
                "200-2",
                "로그인에 성공하였습니다.",
                loginResBody
        );
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public RsData<Empty> logoutMember() {
        memberService.logoutMember();

        return new RsData<>(
                "200-3",
                "로그아웃에 성공하였습니다."
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public RsData<MemberResBody> me() {
        Member actor = memberAuthService.getUserIdentity();
        Member realActor = memberService.getRealActor(actor);

        return new RsData<>(
                "200-4",
                "마이 페이지 접근에 성공하였습니다.",
                MemberResBody.fromEntity(realActor)
        );
    }

}
