package ru.yandex.practicum.filmorate.dao.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper implements ResultSetExtractor<List<User>> {

    private final JdbcTemplate jdbcTemplate;

    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<User> users = new ArrayList<>();
        while(rs.next()){
            User user = new User(rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthdate").toLocalDate());
            user.setId(rs.getInt("userId"));
            users.add(user);
            SqlRowSet getUserFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship " +
                    "WHERE userId=?", user.getId());
            while(getUserFriends.next()){
                user.addFriendId(getUserFriends.getInt("friendId"));
            }
        }
        return users;
    }
}
