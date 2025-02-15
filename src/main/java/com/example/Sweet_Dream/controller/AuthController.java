package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.exception.InvalidTokenException;
import com.example.Sweet_Dream.jwt.JWTUtil;
import com.example.Sweet_Dream.service.AuthService;
import com.example.Sweet_Dream.util.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    public AuthController(AuthService authService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰을 찾을 수 없습니다.");
        }

        try {
            String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            String newAccessToken = authService.refreshAccessToken(userId, refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰 형식이 잘못되었습니다.");
        } catch (SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰 서명이 잘못되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        // 리프레시 토큰이 없으면 403 반환
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("리프레시 토큰이 존재하지 않습니다.");
        }

        try {
            authService.logout(refreshToken);  // 로그아웃 처리 (리프레시 토큰 삭제)
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 리프레시 토큰입니다.");
        }

        // 쿠키에서 refresh_token 삭제
        CookieUtils.deleteCookie(response, "refresh_token");

        return ResponseEntity.ok("로그아웃 성공");
    }
}
