package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Set<Map.Entry<Integer, User>> userMap() {
        return users.entrySet();
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || (!user.getEmail().contains("@"))) {
            log.debug("Электронная почта не может быть пустой и должна содержать символ @.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        } else if (user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            users.put(user.getId(), user);
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Дата рождения не может быть позже настоящего времени.");
            throw new ValidationException("Дата рождения не может быть позже настоящего времени.");
        } else {
            users.put(user.getId(), user);
            log.info("Создан новый пользователь");
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(User user){
        if(users.containsKey(user.getId())) {
            if (user.getEmail() == null || user.getEmail().isBlank() || (!user.getEmail().contains("@"))) {
                log.debug("Электронная почта не может быть пустой и должна содержать символ @.");
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
            } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.debug("Логин не может быть пустым и содержать пробелы.");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
            } else if (user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
                users.put(user.getId(), user);
            } else if (user.getBirthday().isAfter(LocalDate.now())) {
                log.debug("Дата рождения не может быть позже настоящего времени.");
                throw new ValidationException("Дата рождения не может быть позже настоящего времени.");
            } else {
                users.put(user.getId(), user);
                log.info("Данные пользователя с " + user.getId() + "id обновлены.");
            }
            return user;
        }
        log.info("Такого пользователя не существует");
        return user;
    }
}
    //        if (user.getBirthday().isAfter(ChronoLocalDate.from(Instant.now()))){
//            throw new ValidationException("дата рождения не может быть в будущем.");
//        }
//    электронная почта не может быть пустой и должна содержать символ @;
//    логин не может быть пустым и содержать пробелы;
//    имя для отображения может быть пустым — в таком случае будет использован логин;
//    дата рождения не может быть в будущем.

