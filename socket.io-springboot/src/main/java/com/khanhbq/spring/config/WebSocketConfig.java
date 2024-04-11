package com.khanhbq.spring.config;

import com.khanhbq.socket.SpringWebSocketWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SpringWebSocketWrapper mSpringWebSocketWrapper;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mSpringWebSocketWrapper, "/chat/ws/")
                .addInterceptors(mSpringWebSocketWrapper)
                .setAllowedOrigins("*");
    }
}
