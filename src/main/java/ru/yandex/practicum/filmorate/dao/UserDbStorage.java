package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("userDBStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final UserMapper userMapper;


    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userMapper);
    }

    @Override
    public User getUserById(int id) {
        if(checkUserInDb(id)){
            String sql = "SELECT * FROM users WHERE userId="+id;
            return jdbcTemplate.query(sql, this::makeUser);
        }else{
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (NAME, EMAIL, LOGIN, BIRTHDATE) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    new String[]{"userid"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(generatedId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(checkUserInDb(user.getId())){
            jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birthdate=? WHERE userId=?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        }
        return user;
    }

    @Override
    public User deleteUserById(int userId) {
        Optional<User> userOptional = Optional.of(getUserById(userId));
        if(userOptional.isPresent()) {
            jdbcTemplate.update("DELETE FROM friendship where userId = ?", userId);
            jdbcTemplate.update("DELETE FROM friendship where friendId = ?", userId);
            jdbcTemplate.update("DELETE FROM likesList where userId = ?", userId);
            jdbcTemplate.update("DELETE FROM users where userId = ?", userId);
            log.info("Пользователь с userId " + userId + " был удален.");
            return userOptional.get();
        } else {
            log.info("Пользователь с userId " + userId + " не был удален.");
            throw new NotFoundObjectException("Пользователь с userId " + userId + " не был удален.");
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if(checkUserInDb(userId)&&checkUserInDb(friendId)){
            List<Integer> friends = new ArrayList<>();
            SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship " +
                    "WHERE userId=?", friendId);
            while(checkFriends.next()){
                friends.add(checkFriends.getInt("friendId"));
            }
            if (friends.contains(userId)){
                jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId,
                        jdbcTemplate.queryForObject("SELECT friendshipStatusId FROM friendshipStatus WHERE description='apply'",
                                Integer.class));
                jdbcTemplate.update("UPDATE friendship SET friendshipStatusId=? WHERE userId=? AND friendId=?",
                        jdbcTemplate.queryForObject("SELECT friendshipStatusId FROM friendshipStatus WHERE description='apply'",
                                Integer.class), friendId, userId);

            }else{
                jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId,
                        jdbcTemplate.queryForObject("SELECT friendshipStatusId FROM friendshipStatus WHERE description='not apply'",
                                Integer.class));
          }
        }
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE userId=? AND friendId=?", userId, friendId);
        List<Integer> friends = new ArrayList<>();
        SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship " +
                "WHERE userId=?", userId);

        while(checkFriends.next()){
            friends.add(checkFriends.getInt("friendId"));
        }

        if(friends.contains(userId)){
            jdbcTemplate.update("UPDATE friendship SET friendshipStatusId=? WHERE userId=? AND friendId=?",
                    jdbcTemplate.queryForObject("SELECT friendshipStatusId FROM friendshipStatus WHERE description='not apply'",
                            Integer.class), friendId, userId);
        }
        return getUserById(friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        if(checkUserInDb(id)) {
            List<User> userFriends = new ArrayList<>();
            SqlRowSet getFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship WHERE userId=?",
                    id);
            while (getFriends.next()){
                userFriends.add(getUserById(getFriends.getInt("friendId")));
            }
            return userFriends;
        } else {
            return null;
        }
    }


    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        if(checkUserInDb(userId)) {
            List<User> userFriends = new ArrayList<>();
            SqlRowSet getFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship WHERE userId=?",
                    userId);
            while (getFriends.next()){
                userFriends.add(getUserById(getFriends.getInt("friendId")));
            }
            return userFriends;
        } else {
            return null;
        }
    }

    @Override
    public List<Film> getRecommendedFilms(int id) {
        String sql = "SELECT FILMID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPAID FROM FILMS as f JOIN " +
                "(SELECT FILMID as recommended FROM LIKESLIST " +
                "WHERE USERID  = (SELECT USERID FROM LIKESLIST " +
                "    WHERE FILMID IN (SELECT FILMID as userfilmlist from LIKESLIST where USERID = " + id +") " +
                "    AND USERID <> " + id +
                "    GROUP BY USERID " +
                "    ORDER BY count(USERID) DESC " +
                "    LIMIT 1) " +
                "AND FILMID NOT IN " +
                "    (SELECT FILMID as userfilmlist from LIKESLIST where USERID = " + id +")) as Lr " +
                "ON f.FILMID = recommended";

        return jdbcTemplate.query(sql, filmMapper);
    }

    private boolean checkUserInDb(Integer id) {
        String sql = "SELECT userId FROM users where USERID =?";
        SqlRowSet getUsersFromDb = jdbcTemplate.queryForRowSet(sql, id);
        if (!getUsersFromDb.next()) {
            throw new NotFoundObjectException("Пользователя с id" + id + " нет в базе!");
        }
        return true;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        if (rs.next()) {
            User user = new User(rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthdate").toLocalDate());
            user.setId(rs.getInt("userId"));
            SqlRowSet getUserFriends = jdbcTemplate.queryForRowSet("SELECT friendId FROM friendship " +
                    "WHERE userId=?", user.getId());
            while (getUserFriends.next()) {
                user.addFriendId(getUserFriends.getInt("friendId"));
            }
            return user;
        } else {
            return null;
        }
    }
}
