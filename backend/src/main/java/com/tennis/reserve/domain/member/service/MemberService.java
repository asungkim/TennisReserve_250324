package com.tennis.reserve.domain.member.service;

import com.tennis.reserve.domain.member.dto.request.JoinReqForm;
import com.tennis.reserve.domain.member.dto.response.MemberDto;
import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.repository.MemberRepository;
import com.tennis.reserve.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDto createMember(JoinReqForm body) {
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
        return MemberDto.fromEntity(member);
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

}
