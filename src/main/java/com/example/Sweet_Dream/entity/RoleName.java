package com.example.Sweet_Dream.entity;

public enum RoleName {
    USER,
    ADMIN,
    SUPER_ADMIN;

    public static RoleName fromString(String roleName) {
        // "ROLE_" 접두사 제거
        if (roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);  // "ROLE_" 길이는 5
        }

        try {
            return RoleName.valueOf(roleName.toUpperCase());  // 대소문자 구분을 해결하기 위해 toUpperCase() 사용
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + roleName);
        }
    }
}