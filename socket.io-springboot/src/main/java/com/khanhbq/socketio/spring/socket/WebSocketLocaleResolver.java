package com.khanhbq.socketio.spring.socket;

import io.socket.engineio.server.utils.ParseQS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class WebSocketLocaleResolver implements LocaleResolver, HandshakeInterceptor {

    public static final String LANG = "lang";
    public static final Locale DEFAULT_LOCALE = new Locale("vi");

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getParameter(LANG);
        if (lang != null && !lang.isEmpty()) {
            return new Locale(lang);
        }
        return DEFAULT_LOCALE;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // For WebSocket, setting the locale might not be necessary, as it can be immutable once established.
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            final String queryString = request.getURI().getQuery();
            if (queryString != null) {
                Map<String, String> mQuery = ParseQS.decode(queryString);
                String lang = mQuery.get(LANG);
                if (StringUtils.hasText(lang)) {
                    LocaleContextHolder.setLocale(new Locale(lang), true);
                }
            } else {
                LocaleContextHolder.setLocale(DEFAULT_LOCALE, true);
            }
        } catch (Exception ex) {
            log.error("Try to set locale beforeHandshake ERROR", ex);
            LocaleContextHolder.setLocale(DEFAULT_LOCALE, true);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
    }
}
