package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

//    Создайте UserService, который будет отвечать за такие операции с пользователями,
//    как добавление в друзья, удаление из друзей, вывод списка общих друзей.
//    Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
//    То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
    // id друга можно добавить только один раз, нужна обработка исключение и правильная структура хранения Set

    Collection<User> getAll();
    User getById(int id);
    User createUser(User user);
    User updateUser(User user);
    User deleteUserById(int id);
    void addFriend(int userId, int friendId);// отдельное хранилище айдишек друзей
    User deleteFriend(int userId, int friendId);
    List<User> getFriends(int id);//запрос данных из хранилища айдишек друзей
    List<User> getCommonFriends(int userId, int otherId);//метод сравнивающий хранилища двух разных юзеров
}
