package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.User;

public class UserValidator {

    public static User checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
