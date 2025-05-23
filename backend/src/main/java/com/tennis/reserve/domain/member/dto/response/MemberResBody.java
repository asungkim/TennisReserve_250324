package com.tennis.reserve.domain.member.dto.response;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.entity.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberResBody(
        Long id,
        String username,
        String nickname,
        String email,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Role role
) {
    public static MemberResBody fromEntity(Member member) {
        return MemberResBody.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .role(member.getRole())
                .build();
    }
}
