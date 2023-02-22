package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public  class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder().
                timestamp(rs.getLong("timestamp")).
                userId(rs.getInt("userId")).
                eventType(rs.getString("eventType")).
                operation(rs.getString("operation")).
                eventId(rs.getInt("eventId")).
                entityId(rs.getLong("entityId")).
                build();
    }
}

