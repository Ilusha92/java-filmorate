package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    Collection<User> getAll();
    User getById(int id);
    User createUser(User user);
    User updateUser(User user);
    User deleteUserById(int id);
    void addFriend(int userId, int friendId);
    User deleteFriend(int userId, int friendId);
    List<User> getFriends(int id);
    List<User> getCommonFriends(int userId, int otherId);
}
