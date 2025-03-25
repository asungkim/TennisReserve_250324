package com.tennis.reserve.domain.member.dto.response;

import com.tennis.reserve.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt();
        this.modifiedAt = member.getModifiedAt();
    }

    public static MemberDto fromEntity(Member member) {
        return new MemberDto(member);
    }
}
