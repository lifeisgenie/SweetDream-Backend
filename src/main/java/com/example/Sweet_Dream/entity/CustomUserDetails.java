package com.example.Sweet_Dream.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User userEntity;

    public CustomUserDetails(User userEntity) {

        this.userEntity = userEntity;
    }

    // role 값을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // RoleName enum을 String으로 변환하여 SimpleGrantedAuthority에 전달
        authorities.add(new SimpleGrantedAuthority(userEntity.getRole().toString()));

        return authorities;
    }

    // 비밀번호 반환.
    @Override
    public String getPassword() {

        return userEntity.getPassword();
    }

    // 아이디 반환.
    @Override
    public String getUsername() {

        return userEntity.getUsername();
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
