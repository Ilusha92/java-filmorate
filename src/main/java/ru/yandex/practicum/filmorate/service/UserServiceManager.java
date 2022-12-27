package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceManager implements UserService{

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Override
    public Collection<User> getAll() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getById(int id) {
        User user = userStorage.getUserById(id);

        if (user == null) {
            throw new NotFoundObjectException("Can't find this user");
        }

        return user;
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
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.addFriendId(friendId);
        friend.addFriendId(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) throws ValidationException {
        if (userId == friendId) {
            throw new ValidationException("friendId can not be equal to userId");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null) {
            throw new NotFoundObjectException("user");
        }

        if (friend == null) {
            throw new NotFoundObjectException("user");
        }
        Set<Integer> currentUserFriends = new LinkedHashSet<>(user.getFriendIds());
        Set<Integer> currentFriendFriends = new LinkedHashSet<>(friend.getFriendIds());

        currentUserFriends.add(friendId);
        currentFriendFriends.add(userId);

        userStorage.updateUser(userStorage.getUserById(friendId));
        return userStorage.updateUser(userStorage.getUserById(userId));
    }

    @Override
    public List<User> getFriends(int id) {
        User user = userStorage.getUserById(id);

        if (user == null) {
            throw new NotFoundObjectException("user");
        }

        return user
                .getFriendIds()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        if (user == null) {
            throw new NotFoundObjectException("user");
        }

        if (otherUser == null) {
            throw new NotFoundObjectException("otherUser");
        }

        return user
                .getFriendIds()
                .stream()
                .filter(friendId -> otherUser.getFriendIds().contains(friendId))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
