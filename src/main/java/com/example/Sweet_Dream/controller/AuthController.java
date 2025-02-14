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

    // 생성자 주입을 통해 JWTUtil을 주입받습니다.
    public AuthController(AuthService authService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // 리프레시 토큰으로 새로운 액세스 토큰을 생성
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        // 쿠키에서 리프레시 토큰을 가져옴
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        if (refreshToken == null) {
            System.out.println("리프레시 토큰이 null입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("리프레시 토큰을 찾을 수 없습니다.");
        }

        try {
            // 리프레시 토큰에서 userId를 추출
            String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);

            // 리프레시 토큰을 이용해 새로운 액세스 토큰을 생성
            String newAccessToken = authService.refreshAccessToken(userId, refreshToken);

            return ResponseEntity.ok(newAccessToken);  // 새로운 액세스 토큰을 반환
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

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        // 디버깅: 리프레시 토큰 값 출력
        System.out.println("로그아웃 요청: refresh_token = " + refreshToken);

        // 리프레시 토큰이 없는 경우 처리
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("리프레시 토큰이 존재하지 않습니다.");
        }

        try {
            authService.logout(refreshToken);  // 리프레시 토큰이 유효한지 확인하고 로그아웃 처리
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 리프레시 토큰입니다.");
        }

        // 쿠키에서 refresh_token 삭제
        CookieUtils.deleteCookie(response, "refresh_token");

        return ResponseEntity.ok("로그아웃 성공");
    }
}
