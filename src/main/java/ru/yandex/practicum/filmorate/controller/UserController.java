package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();
    private int userId = 0;

    private int generateUserId() {
        userId++;
        return userId;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateUserId());
        users.put(user.getId(), user);
        log.info("Список юзеров пополнен.");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Такого юзера нет в коллекции");
        }
        return user;
    }

//    public boolean validate(User user) throws ValidationException {
//        if (user.getEmail() == null || user.getEmail().isBlank() || (!user.getEmail().contains("@"))) {
//            log.debug("Электронная почта не может быть пустой и должна содержать символ @.");
//            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
//        }
//        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
//            log.debug("Логин не может быть пустым и содержать пробелы.");
//            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
//        }
//        if (user.getBirthday().isAfter(LocalDate.now())) {
//            log.debug("Дата рождения не может быть позже настоящего времени.");
//            throw new ValidationException("Дата рождения не может быть позже настоящего времени.");
//        }
//
//        return false;
//    }

}



