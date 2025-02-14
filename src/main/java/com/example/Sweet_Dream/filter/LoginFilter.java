package com.example.Sweet_Dream.filter;

import com.example.Sweet_Dream.entity.CustomUserDetails;
import com.example.Sweet_Dream.entity.RefreshToken;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.jwt.JWTUtil;
import com.example.Sweet_Dream.repository.RefreshTokenRepository;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 생성자 주입을 통해 의존성 해결
    @Autowired
    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/accounts/login"); // 로그인 요청 URL
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

        // authentication.getAuthorities()에서 role 정보 추출
        String role = authentication.getAuthorities()
                .stream()
                .findFirst() // 첫 번째 권한을 가져옴
                .map(GrantedAuthority::getAuthority) // 권한을 String으로 가져옴
                .orElseThrow(() -> new RuntimeException("Role not found")); // 권한이 없으면 예외를 던짐

        // "ROLE_"을 제외한 실제 role 이름만 추출
        String roleNameString = role.replace("ROLE_", "").toUpperCase(); // 대소문자 구분을 해결하기 위해 toUpperCase() 사용

        // RoleName enum으로 변환
        RoleName roleEnum = RoleName.valueOf(roleNameString);  // 이제 RoleName에서 ROLE_ 없이 값만 사용

        final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 10L; // 10시간
        final long REFRESH_TOKEN_EXPIRATION = 60 * 60 * 24 * 7L; // 7일

        // JWT 생성
        String accessToken = jwtUtil.createAccessToken(customUserDetails.getUsername(), roleEnum, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createRefreshToken(customUserDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);

        // 리프레시 토큰을 DB에 저장
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION);
        refreshTokenRepository.save(new RefreshToken(refreshToken, customUserDetails.getUser(), expiresAt));

        // JWT를 Authorization 헤더에 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh-Token", refreshToken);

        // 리프레시 토큰을 쿠키에 추가 (30일 유효)
        CookieUtils.addCookie(response, "refresh_token", refreshToken, 60 * 60 * 24 * 30); // 30일 유효

        // 응답을 JSON 형태로 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}");
    }

    // 로그인 실패 시 오류 메시지 반환
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication failed: " + failed.getMessage());
    }
}
