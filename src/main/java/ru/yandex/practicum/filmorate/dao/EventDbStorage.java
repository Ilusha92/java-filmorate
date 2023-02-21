package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.EventMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDbStorage {
    private final JdbcTemplate jdbcTemplate;


    public void saveEvent(long userId, EventTypes eventType, OperationTypes operation, long entityId) {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        jdbcTemplate.update("INSERT INTO events(TIMESTAMP, USERID, EVENTTYPE, OPERATION, ENTITYID) VALUES (?,?,?,?,?)",
                timestamp.getTime() , userId, eventType.name(), operation.name(), entityId);
    }

    public List<Event> getEvent(int id) {
        if (!jdbcTemplate.queryForRowSet("SELECT USERID FROM USERS WHERE USERID =?", id).next()) {
         throw new NotFoundObjectException("User with id " + id + " not found");
        }
        return jdbcTemplate.queryForStream("SELECT * " +
                "FROM EVENTS " +
                "WHERE USERID = ?;", (rs, rowNum) ->
                new EventMapper().mapRow(rs, rowNum), id).collect(Collectors.toList());
    }
}
