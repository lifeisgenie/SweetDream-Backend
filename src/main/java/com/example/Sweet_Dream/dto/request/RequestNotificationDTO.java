package com.example.Sweet_Dream.dto.request;

import lombok.Data;

@Data
public class RequestNotificationDTO {
    private String userId; // 알림 받을 사용자 Id;
    private String title; // 알림 제목
    private String content; // 알림 내용
}
