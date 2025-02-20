package com.example.Sweet_Dream.controller;

import com.example.Sweet_Dream.dto.request.RequestNotificationDTO;
import com.example.Sweet_Dream.dto.response.ResponseNotificationDTO;
import com.example.Sweet_Dream.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import jakarta.validation.Valid;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    // WebSocket을 통한 알림 전송
    @MessageMapping("/sendNotification") // 클라이언트에서 "/app/sendNotification" 경로로 보냄
    public void sendNotificationWebSocket(@Payload @Valid RequestNotificationDTO request) {
        try {
            ResponseNotificationDTO responseDTO = notificationService.sendNotification(request);
            messagingTemplate.convertAndSend("/topic/notifications/" + request.getUserId(), responseDTO);
        } catch (Exception e) {
            System.err.println("WebSocket 알림 전송 실패: " + e.getMessage());
        }
    }


    // HTTP API를 통한 알림 전송
    @PostMapping("/send")
    public ResponseNotificationDTO sendNotification(@RequestBody RequestNotificationDTO request) {
        try {
            return notificationService.sendNotification(request);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("알림 전송 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("알림 처리 중 오류 발생");
        }
    }

    // 특정 사용자의 알림 목록 조회
    @GetMapping("/{userId}")
    public List<ResponseNotificationDTO> getUserNotifications(@PathVariable("userId") String userId) {
        try {
            return notificationService.getUserNotifications(userId);
        } catch (Exception e) {
            throw new RuntimeException("알림 조회 실패: " + e.getMessage());
        }
    }
}
