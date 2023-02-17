package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceManager implements UserService{

    private final UserStorage userStorage;

    @Autowired
    public UserServiceManager(@Qualifier("userDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getById(int id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public User deleteUserById(int id) {
        return userStorage.deleteUserById(id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) throws ValidationException {
        return userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> user1Friends = userStorage.getFriends(userId);
        List<User> user2Friends = userStorage.getFriends(otherId);
        for(User user : user1Friends){
            if(user2Friends.contains(user)){
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }

    @Override
    public List<Film> getRecommendedFilms(int id) {
        return userStorage.getRecommendedFilms(id);
    }
}
