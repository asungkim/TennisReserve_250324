package com.tennis.reserve.domain.member.entity;

import com.tennis.reserve.domain.base.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
public class Member extends BaseTime {

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    public boolean isManager() {
        return role.equals(Role.MANAGER);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getMemberAuthoritiesAsString()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

    }

    public List<String> getMemberAuthoritiesAsString() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin()) {
            authorities.add("ROLE_ADMIN");
        } else if (isManager()) {
            authorities.add("ROLE_MANAGER");
        }

        return authorities;
    }
}
