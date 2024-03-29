package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    
    List<User> getAllUsers();
    User getUserById(int userId);
    User createUser(User user);
    User updateUser(User user);
    User deleteUserById(int userId);
    void addFriend(int userId, int friendId);
    User deleteFriend(int userId, int friendId);
    List<User> getFriends(int id);
    List<User> getCommonFriends(int userId, int otherId);
}
