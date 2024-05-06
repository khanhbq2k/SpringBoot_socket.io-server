package com.khanhbq.socketio.spring.socket.config;

import com.khanhbq.socketio.spring.socket.WebSocketLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSocketLocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        return new WebSocketLocaleResolver();
    }
}
