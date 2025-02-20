package com.example.Sweet_Dream.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId; // 고유 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user_id; // 알림 받는 사용자

    @Column(name = "title", nullable = false)
    private String title; // 알림 제목

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // 알림 내용

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
