package com.khanhbq.socketio.spring.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoSocket;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class SocketController implements CommandLineRunner {

    public static final String LANG = "lang";
    public static final Locale DEFAULT_LOCALE = new Locale("vi");

    private final SocketIoNamespace mainNamespace;
    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... xargs) {
        mainNamespace.on("connection", args -> {
            SocketIoSocket socket = (SocketIoSocket) args[0];
            String subject = socket.getInitialQuery().get("subject");
            sessionManager.addSession(socket.getId(), subject);
            socket.on("disconnect", arr -> {
                sessionManager.removeSession(socket.getId());
            });
            // The event and ChatMessage format correspond to {@link resources/test.html}
            socket.on("chat", arr -> handleCommand(socket, (JSONObject) arr[0], jsonData -> {
                ChatMessage data;
                try {
                    data = objectMapper.readValue(jsonData.toString(), ChatMessage.class);
                    // broadcast all rooms
                    socket.broadcast(null, "chat", new JSONObject(objectMapper.writeValueAsString(data)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("data: " + data.getMsg());
            }));
        });
    }

    private void handleCommand(SocketIoSocket socket, JSONObject jsonData, Consumer<JSONObject> commandHandler) {
        Locale locale = Optional.ofNullable(socket.getInitialQuery().get(LANG))
                .map(String::valueOf)
                .map(Locale::new)
                .orElse(DEFAULT_LOCALE);
        LocaleContextHolder.setLocale(locale, true);

        try {
            commandHandler.accept(jsonData);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}