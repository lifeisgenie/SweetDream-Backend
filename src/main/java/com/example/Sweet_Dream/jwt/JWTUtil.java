package com.example.Sweet_Dream.jwt;

import com.example.Sweet_Dream.entity.RoleName;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    // 생성자: application.properties에 설정된 비밀 키를 사용하여 SecretKeySpec을 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        // application.properties에서 가져온 secret 값을 기반으로 HS256 알고리즘으로 키 생성
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * JWT에서 username을 추출하는 메서드
     * @param token JWT 토큰
     * @return username
     */
    public String getUsername(String token) {
        try {
            // JWT를 파싱하여 "username" 클레임을 추출
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("username", String.class);
        } catch (JwtException e) {
            // 예외 발생 시 null 반환 또는 필요 시 로깅 처리
            return null;
        }
    }

    /**
     * JWT에서 role을 추출하는 메서드
     * @param token JWT 토큰
     * @return role
     */
    public String getRole(String token) {
        try {
            // JWT를 파싱하여 "role" 클레임을 추출
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (JwtException e) {
            // 예외 발생 시 null 반환 또는 필요 시 로깅 처리
            return null;
        }
    }

    /**
     * JWT가 만료되었는지 확인하는 메서드
     * @param token JWT 토큰
     * @return true/false 만료 여부
     */
    public Boolean isExpired(String token) {
        try {
            // JWT를 파싱하여 만료일자를 가져와 현재 시간과 비교
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            // 만약 토큰이 만료되었으면 true 반환
            return true;
        } catch (JwtException e) {
            // JWT 파싱 또는 다른 예외 발생 시 false 반환
            return false;
        }
    }

    /**
     * 액세스 토큰을 생성하는 메서드
     * @param userId 사용자 ID
     * @param role 사용자 역할
     * @param expiredMs 토큰 만료 시간 (밀리초)
     * @return 생성된 액세스 토큰
     */
    public String createAccessToken(String userId, RoleName role, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId) // 사용자 ID 저장
                .claim("role", role.name()) // 사용자 역할 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간 설정
                .signWith(secretKey) // 비밀 키로 서명
                .compact(); // JWT 생성
    }

    /**
     * 리프레시 토큰을 생성하는 메서드
     * @param userId 사용자 ID
     * @param expiredMs 토큰 만료 시간 (밀리초)
     * @return 생성된 리프레시 토큰
     */
    public String createRefreshToken(String userId, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId) // 사용자 ID 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간 설정
                .signWith(secretKey) // 비밀 키로 서명
                .compact(); // JWT 생성
    }

    /**
     * 리프레시 토큰에서 userId를 추출하는 메서드
     * @param token 리프레시 토큰
     * @return userId
     */
    public String getUserIdFromRefreshToken(String token) {
        try {
            // JWT를 파싱하여 "userId" 클레임을 추출
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class);
        } catch (JwtException e) {
            // 예외 발생 시 null 반환 또는 필요 시 로깅 처리
            return null;
        }
    }
}
