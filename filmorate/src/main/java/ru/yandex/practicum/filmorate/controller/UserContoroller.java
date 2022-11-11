package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class UserContoroller {

    //private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public Set<Map.Entry<Integer, User>> userMap() {
        return users.entrySet();
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || (!user.getEmail().contains("@"))){
            throw new ValidationException("Проверьте правильность заполнености поля Email");
        }
        if (users.keySet().equals(user.getEmail())){
            throw new UserAlreadyExistException("Пользователь с такой почтой уже зарегистрирован.");
        } else {
            users.put(user.getEmail(), user);
            return user;
        }
    }
//    электронная почта не может быть пустой и должна содержать символ @;
//    логин не может быть пустым и содержать пробелы;
//    имя для отображения может быть пустым — в таком случае будет использован логин;
//    дата рождения не может быть в будущем.
}
