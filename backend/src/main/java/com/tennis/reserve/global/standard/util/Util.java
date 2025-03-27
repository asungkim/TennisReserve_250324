package com.tennis.reserve.global.standard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
public class Util {
    public static class Json {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static String toString(Object obj) {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Jwt {
        public static boolean isWellFormedToken(String token) {
            // 최소한 JWT는 두 개의 점(.)이 있어야 함: header.payload.signature
            return token != null && token.split("\\.").length == 3;
        }

        public static String createToken(String keyString, int expireSeconds, Map<String, Object> claims) {

            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }

        public static boolean isValidToken(String keyString, String token) {

            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(token);

            } catch (MalformedJwtException e) {
                log.warn("잘못된 JWT 형식입니다: {}", e.getMessage());
                return false;
            } catch (Exception e) {
                log.warn("JWT 파싱 중 오류: {}", e.getMessage());
                return false;
            }

            return true;
        }

        public static Map<String, Object> getPayload(String keyString, String jwtStr) {

            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            try {
                return Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwtStr)
                        .getPayload();
            } catch (ExpiredJwtException e) {
                return e.getClaims(); // 만료됐지만 payload 꺼내기 가능
            } catch (JwtException | IllegalArgumentException e) {
                // 그 외의 파싱 불가 상황
                log.warn("Invalid JWT format or structure: {}", e.getMessage());
                return null;
            }
        }
    }


}
