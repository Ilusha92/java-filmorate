package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceManager implements UserService{

    private final UserStorage userStorage;
    private final EventDbStorage eventDbStorage;
    private final GenreService genreService;

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
        if ((user.getName()==null)||(user.getName().isBlank())) {
            log.info("Поле \"Имя\" пустое, ему будет присвоено значение поля \"Логин\"");
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        eventDbStorage.saveEvent(userId, EventTypes.FRIEND, OperationTypes.ADD, friendId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) throws ValidationException {
        User user = userStorage.deleteFriend(userId, friendId);
        eventDbStorage.saveEvent(userId, EventTypes.FRIEND, OperationTypes.REMOVE, friendId);
        return user;
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
        List<Film> recommendedFilms = userStorage.getRecommendedFilms(id);
        Map<Integer, Set<Genre>> allGenres = genreService.getAllGenresOfAllFilms();
        for (Film film : recommendedFilms) {
            film.setGenres(allGenres.get(film.getId()));
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());
            }
        }
        return recommendedFilms;
    }

    @Override
    public List<Event> getEvents(int id) {
        return eventDbStorage.getEvent(id);
    }

    @Override
    public boolean checkUserInDb(Integer userId) {
        return userStorage.checkUserInDb(userId);
    }
}
