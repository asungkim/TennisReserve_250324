package com.tennis.reserve.global.standard.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public void addCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public void removeCookie(String name) {

        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public String getValueFromCookie(String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
