package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class Event {
    private long timestamp;
    private int userId;
    private String eventType;
    private String operation;
    private int eventId;
    private long entityId;

    public Event(long timestamp, int userId, String eventType, String operation, int eventId, long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.eventId = eventId;
        this.entityId = entityId;
    }
}