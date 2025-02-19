package com.example.Sweet_Dream.filter;

import com.example.Sweet_Dream.entity.CustomUserDetails;
import com.example.Sweet_Dream.entity.RefreshToken;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.jwt.JWTUtil;
import com.example.Sweet_Dream.repository.RefreshTokenRepository;
import com.example.Sweet_Dream.service.AuthService;
import com.example.Sweet_Dream.util.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class SignInFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    @Autowired
    public SignInFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                        RefreshTokenRepository refreshTokenRepository, AuthService authService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authService = authService;
        setFilterProcessesUrl("/accounts/signin"); // 로그인 요청 URL
    }

    // 로그인 인증을 위한 attempt 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 요청 본문에서 JSON을 직접 파싱하여 userId와 password 추출
            Map<String, String> credentials = objectMapper.readValue(request.getReader(), Map.class);
            String userId = credentials.get("userId");
            String password = credentials.get("password");

            // UsernamePasswordAuthenticationToken을 사용하여 사용자 인증 정보 설정
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password);

            // AuthenticationManager로 인증 요청
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationException("Failed to parse login request") {};
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        // 리프레시 토큰이 존재하는 경우, 블랙리스트 체크
        if (refreshToken != null && authService.isTokenBlacklisted(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Blacklisted refresh token");
            return; // 블랙리스트에 있는 경우 로그인 시도 차단
        }

        RoleName roleEnum = customUserDetails.getRoleName();
        String roleNameString = roleEnum.name();

        final long ACCESS_TOKEN_EXPIRATION = 100L * 1000; // 10시간
        final long REFRESH_TOKEN_EXPIRATION = 60 * 60 * 24 * 7L; // 7일

        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(customUserDetails.getUsername(), roleEnum, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createRefreshToken(customUserDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);

        // 리프레시 토큰을 DB에 저장
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION);
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, customUserDetails.getUser(), expiresAt));

        // JWT를 Authorization 헤더에 추가
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 리프레시 토큰을 쿠키에 추가
        CookieUtils.addCookie(response, "refresh_token", newRefreshToken, 60 * 60 * 24 * 30); // 30일 유효

        // 응답을 JSON 형태로 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\", \"role\": \"" + roleNameString + "\"}");
    }

    // 로그인 실패 시 오류 메시지 반환
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication failed: " + failed.getMessage());
    }
}

