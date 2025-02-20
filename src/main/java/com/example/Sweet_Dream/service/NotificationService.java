package com.example.Sweet_Dream.service;

import com.example.Sweet_Dream.dto.request.RequestNotificationDTO;
import com.example.Sweet_Dream.dto.response.ResponseNotificationDTO;
import com.example.Sweet_Dream.entity.Notification;
import com.example.Sweet_Dream.entity.User;
import com.example.Sweet_Dream.repository.AccountRepository;
import com.example.Sweet_Dream.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public NotificationService(NotificationRepository notificationRepository, AccountRepository accountRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // 알림 생성 및 전송
    @Transactional
    public ResponseNotificationDTO sendNotification (RequestNotificationDTO request) {
        User user = findUserById(request.getUserId());
        Notification notification = createNotification(user, request.getTitle(), request.getContent());

        Notification savedNotification = notificationRepository.save(notification);
        ResponseNotificationDTO responseDTO = toResponseDTO(savedNotification);

        sendWebSocketNotification(user.getUserId(), responseDTO);
        return responseDTO;
    }

    // 특정 사용자의 모든 알림 조회
    @Transactional
    public List<ResponseNotificationDTO> getUserNotifications(String userId) {
        return notificationRepository.findByUser_UserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 사용자 조회 (없는 경우 예외 발생)
    private User findUserById(String userId) {
        User user = accountRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    // 알림 객체 생성
    private Notification createNotification(User user, String title, String content) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        return notification;
    }

    // Response DTO 변환
    private ResponseNotificationDTO toResponseDTO(Notification notification) {
        return new ResponseNotificationDTO(
                notification.getNotificationId(),
                notification.getUser().getUserId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getCreatedAt().toString(),
                notification.getUpdatedAt().toString()
        );
    }

    // WebSocket을 통해 알림 전송
    private void sendWebSocketNotification(String userId, ResponseNotificationDTO responseDTO) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, responseDTO);
    }
}
