package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(unique = true, nullable = false)
    private String userId;  // 사용자 ID를 기본키로 사용

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;  // 외래키로 Role 엔티티 참조

    @Column(nullable = false)
    private String username;  // 사용자 이름

    @Column(unique = true, nullable = false)
    private String email;  // 사용자 이메일

    @Column(nullable = false)
    private String password;  // 비밀번호
}
