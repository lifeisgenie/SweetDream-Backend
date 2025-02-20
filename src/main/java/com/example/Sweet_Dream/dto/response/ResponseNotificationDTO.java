package com.example.Sweet_Dream.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ResponseNotificationDTO {
    private Long notification_id; // 알림 고유 ID
    private String userId; // 알림 받을 사용자 ID
    private String title; // 알림 제목
    private String content; // 알림 내용
    private String created_at; // 알림 생성 시간
    private String updated_at; // 알림 수정 시간
}
