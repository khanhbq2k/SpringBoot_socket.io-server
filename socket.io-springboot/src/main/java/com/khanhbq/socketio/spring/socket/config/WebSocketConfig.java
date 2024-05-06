package com.khanhbq.socketio.spring.socket.config;

import com.khanhbq.socketio.spring.socket.SpringWebSocketWrapper;
import com.khanhbq.socketio.spring.socket.WebSocketLocaleResolver;
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
    private final WebSocketLocaleResolver webSocketLocaleResolver;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mSpringWebSocketWrapper, "/chat/ws/")
                .addInterceptors(mSpringWebSocketWrapper)
                .addInterceptors(webSocketLocaleResolver)
                .setAllowedOrigins("*");
    }
}
