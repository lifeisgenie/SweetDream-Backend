package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.jwt.JWTUtil;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private JWTUtil jwtUtil;

    public TokenService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 예시: 리프레시 토큰 생성
    public String createRefreshToken(String username) {
        // 예시: 30일(30 * 24 * 60 * 60 * 1000) 후 만료되도록 설정
        long expirationTime = 30 * 24 * 60 * 60 * 1000L;

        // JWTUtil의 createRefreshToken 메서드를 호출할 때 만료 시간을 전달
        return jwtUtil.createRefreshToken(username, expirationTime);
    }
}
