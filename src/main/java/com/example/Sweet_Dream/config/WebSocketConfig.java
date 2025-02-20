package com.example.Sweet_Dream.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 엔드포인트를 /ws로 설정
                .setAllowedOrigins("*") // CORS 설정
                .withSockJS(); // WebSocket 미지원 브라우저에서 연결 가능하도록 SockJS 사용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 클라이언트에게 메시지 전달하는 경로
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 서버로 메시지 보낼 때 사용 경로
    }
}
