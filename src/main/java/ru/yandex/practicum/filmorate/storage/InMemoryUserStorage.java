package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    private int generateUserId() {
        return ++userId;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        UserValidator.checkName(user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        } else {
            throw new NotFoundObjectException("Невозможно обновить данные. Такого пользователя не существует.");
        }
        return user;
    }

    @Override
    public User deleteUserById(int userId) {
        User user = users.get(userId);
        users.remove(userId);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(int id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return null;
    }

    @Override
    public List<Film> getRecommendedFilms(int id) {
        return null;
    }

    @Override
    public List<Event> getEvents(int id) {
        return null;
    }

}
