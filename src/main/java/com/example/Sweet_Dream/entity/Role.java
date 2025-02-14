package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;  // 역할 ID

    @Enumerated(EnumType.STRING) // role_name을 ENUM으로 처리
    @Column(name = "role_name", unique = true)
    private RoleName roleName; // 역할 이름 (user, admin, super_admin)

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성 시간
}