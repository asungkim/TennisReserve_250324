package com.tennis.reserve.global.security;

import com.tennis.reserve.domain.member.entity.Member;
import com.tennis.reserve.domain.member.service.MemberAuthService;
import com.tennis.reserve.domain.member.service.MemberRedisService;
import com.tennis.reserve.domain.member.service.MemberService;
import com.tennis.reserve.global.standard.util.Rq;
import com.tennis.reserve.global.standard.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    // 로그인이 필요하지 않은 uri
    private static final Set<String> WHITELIST = Set.of(
            "/api/members/login",
            "/api/members/join"
    );

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;
    private final Rq rq;
    private final MemberRedisService memberRedisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 로그인이 필요하지 않은 기능은 내보냄
        if (WHITELIST.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 로그인이 필요한 기능인 경우 프론트가 보낸 토큰을 가져옴
        String accessToken = getAccessTokenFromRequest(request);

        // 3. accessToken이 null이면 프론트가 안보낸거니까 filter 나감
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!Util.Jwt.isWellFormedToken(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. accessToken이 유효하면 가져와서 로그인 처리
        Optional<Member> validMember = memberAuthService.getValidMemberByAccessToken(accessToken);
        if (validMember.isPresent()) {
            Member actor = validMember.get();
            memberAuthService.setLogin(actor);
            filterChain.doFilter(request, response);
            return;
        }

        // 5-1 accessToken이 유효하지 않으면 accessToken 토큰을 통해 유저 정보 가져옴
        Optional<Member> opActor = memberAuthService.getMemberByAccessToken(accessToken);

        // 5-2 가져온 유저 정보로 refreshToken을 구함
        if (opActor.isPresent()) {
            Member actor = opActor.get();// payload로 가져온 멤버
            Member realActor = memberService.findById(actor.getId()).get();
            Optional<String> opRefreshToken = memberRedisService.get(realActor);

            // 5-3 refreshToken이 유효하면 newAccessToken 만들어서 쿠키에 등록
            if (opRefreshToken.isPresent()) {
                String newAccessToken = memberAuthService.generateAccessToken(actor);
                rq.addCookie("accessToken", newAccessToken, 60 * 60);
                memberAuthService.setLogin(actor);
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                        "code": "401-2",
                        "message": "유효하지 않은 인증 토큰입니다. 다시 로그인해주세요."
                    }
                    """);
            return;
        }


        // ✅ 7. accessToken 만료 + refreshToken 만료 → 401 직접 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                    {
                        "code": "401-1",
                        "message": "로그인 정보가 만료되었습니다. 다시 로그인해주세요."
                    }
                """);
    }


    private String getAccessTokenFromRequest(HttpServletRequest request) {
        // 1. 헤더로부터 토큰 정보 가져옴
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        // 2. 헤더 없으면 쿠키로부터 정보 가져옴
        String accessToken = rq.getValueFromCookie("accessToken");

        // 3. 쿠키에 있으면 리턴
        if (accessToken != null) {
            return accessToken;
        }

        // 4. 쿠키에도 없으면 null 리턴
        return null;
    }
}
