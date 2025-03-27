package com.tennis.reserve.global.security;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class SecurityUser extends User implements OAuth2User {

    @Getter
    private Long id;

    @Getter
    private Role role;

    @Getter
    private String nickname;

    public SecurityUser(Long id,
                        String username,
                        String password,
                        String nickname,
                        Role role,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
        this.nickname = nickname;
    }

    public SecurityUser(Member member) {
        this(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getNickname(),
                member.getRole(),
                member.getAuthorities());
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
