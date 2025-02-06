package com.example.Sweet_Dream.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        // 우리의 모든 컨트롤러 경로에 대해서
        corsRegistry.addMapping("/**")
                // 프론트엔드에서 요청이 오는 주소를 넣으면 됨.
                .allowedOrigins("http://localhost:3000");
    }
}
