package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Set<Map.Entry<Integer, Film>> filmMap() {
        return films.entrySet();
    }

}
