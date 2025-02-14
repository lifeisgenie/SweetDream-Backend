package com.example.Sweet_Dream.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CustomUserDetails implements UserDetails {

    private final User user;  // 사용자 정보 (User 객체)
    private List<Role> roles;  // 역할 정보 (Role 객체)

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // role 값을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().name()));
    }

    // 비밀번호 반환.
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 아이디 반환.
    @Override
    public String getUsername() {
        return user.getUserId();  // userId를 username으로 사용
    }

    // 계정이 만료되지 않았음을 확인.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠금되지 않았음 확인.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
