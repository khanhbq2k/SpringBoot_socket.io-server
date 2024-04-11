package com.khanhbq.socket;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<String, String> sessionToSubject = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> subjectToSessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, String subject) {
        subjectToSessions.compute(subject, (id, sessions) -> {
            if (sessions != null) {
                sessions.add(sessionId);
                return sessions;
            } else {
                return new HashSet<>(Set.of(sessionId));
            }
        });
        sessionToSubject.put(sessionId, subject);
    }

    public void removeSession(String sessionId) {
        var socketSubject = sessionToSubject.remove(sessionId);
        if (socketSubject != null) {
            subjectToSessions.computeIfPresent(socketSubject, (subject, sessions) -> {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    return null;
                }
                return sessions;
            });
        }
    }

    public Set<String> getSessionBySubject(String subjectId) {
        return subjectToSessions.get(subjectId);
    }

    public Set<String> getAllChatSubjects() {
        return subjectToSessions.keySet();
    }
}
