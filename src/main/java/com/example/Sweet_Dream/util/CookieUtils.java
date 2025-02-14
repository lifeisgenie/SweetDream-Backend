package com.example.Sweet_Dream.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    // 쿠키에서 값 가져오기
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 쿠키에 값 설정
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);  // 클라이언트에서 JavaScript로 접근 불가
        cookie.setPath("/");       // 쿠키가 유효한 경로 설정
        cookie.setMaxAge(maxAge);  // 쿠키 만료 시간 설정

        // 로컬 환경에서는 Secure를 false로, 배포 환경에서는 true로 설정
        boolean isSecure = false; // 로컬 환경에서는 false
        if ("true".equals(System.getenv("IS_PRODUCTION"))) { // 예: 배포 환경을 위한 환경 변수 확인
            isSecure = true; // 배포 환경에서는 true
        }
        cookie.setSecure(isSecure); // HTTPS 환경에서만 true

        // 쿠키 헤더에 SameSite 속성 추가 (직접 헤더 수정)
        String cookieHeader = String.format("%s=%s; Path=%s; Max-Age=%d; HttpOnly; Secure=%b; SameSite=None",
                cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getMaxAge(), isSecure);

        // SameSite 속성 추가된 쿠키 헤더 설정
        response.addHeader("Set-Cookie", cookieHeader);
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // 삭제할 때는 Secure를 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 만료 시간 0으로 설정하여 삭제

        // 쿠키 헤더에 SameSite 속성 추가하여 삭제 처리
        String cookieHeader = String.format("%s=%s; Path=%s; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getMaxAge());

        // SameSite 속성 추가된 쿠키 삭제 헤더 설정
        response.addHeader("Set-Cookie", cookieHeader);
    }
}