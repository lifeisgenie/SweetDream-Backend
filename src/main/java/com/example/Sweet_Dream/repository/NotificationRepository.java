package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.Notification;
import com.example.Sweet_Dream.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserId(String userId); // 특정 사용자 알림 리스트 조회 메서드
}
