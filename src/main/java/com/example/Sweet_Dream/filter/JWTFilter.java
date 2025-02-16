package com.example.Sweet_Dream.filter;

import com.example.Sweet_Dream.entity.CustomUserDetails;
import com.example.Sweet_Dream.entity.Role;
import com.example.Sweet_Dream.entity.RoleName;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.jwt.JWTUtil;
import com.example.Sweet_Dream.repository.RoleRepository;
import com.example.Sweet_Dream.repository.AccountRepository;
import com.example.Sweet_Dream.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;

    // 생성자: JWTUtil, RoleRepository, UserRepository 주입
    @Autowired
    public JWTFilter(JWTUtil jwtUtil, RoleRepository roleRepository, AccountRepository accountRepository) {
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        String refreshToken = CookieUtils.getCookieValue(request, "refresh_token");

        String token = null;

        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.split(" ")[1]; // Authorization 헤더에서 액세스 토큰 추출
        } else if (refreshToken != null) {
            token = refreshToken; // 쿠키에서 리프레시 토큰 추출
        }

        if (token != null) {
            boolean isExpired = jwtUtil.isExpired(token);

            if (isExpired) {
                // 토큰 만료 시 401 응답 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            }

            // 액세스 토큰을 사용한 경우
            if (authorization != null) {
                // JWT에서 역할(role)을 추출
                String role = jwtUtil.getRole(token);
                if (role == null || role.isEmpty()) {
                    // 역할이 없으면 401 응답 반환
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Role is missing in the token");
                    return;
                }

                try {
                    // 역할에 해당하는 RoleEntity를 찾음
                    RoleName roleName = RoleName.valueOf(role.toUpperCase());
                    Role roleEntity = roleRepository.findByRoleName(roleName);

                    if (roleEntity != null) {
                        // 사용자 정보 설정
                        User userEntity = new User();
                        userEntity.setUsername(jwtUtil.getUsername(token));
                        userEntity.setRole(roleEntity);

                        // 사용자 정보와 역할을 기반으로 Authentication 객체 생성
                        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
                        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (IllegalArgumentException e) {
                    // 역할이 잘못된 경우 예외 처리
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid role in the token");
                    return;
                }
            }

            // 리프레시 토큰을 사용하는 경우
            if (refreshToken != null) {
                String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
                User user = accountRepository.findByUserId(userId);

                if (user != null) {
                    // 사용자 정보 설정
                    CustomUserDetails customUserDetails = new CustomUserDetails(user);
                    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}