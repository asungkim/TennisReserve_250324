package com.tennis.reserve.domain.member.controller;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.response.MemberDto;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.global.dto.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public RsData<MemberDto> createMember(@RequestBody @Valid JoinReqForm joinReqForm) {
        MemberDto memberDto = memberService.createMember(joinReqForm);

        return new RsData<>(
                "200",
                "회원가입에 성공하였습니다.",
                memberDto
        );
    }
}
