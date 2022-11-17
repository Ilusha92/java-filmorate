package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final LocalDate upperLimit = LocalDate.parse("1895-12-28");

    @GetMapping("/films")
    public Set<Map.Entry<Integer, Film>> filmMap() {
        return films.entrySet();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Название не может быть пустым.");
            throw new ValidationException("Название не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            log.debug("Максимальная длина описания — 200 символов.");
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        } else if (film.getReleaseDate().isBefore(upperLimit)) {
            log.debug("Дата релиза — не раньше 1895.12.28.");
            throw new ValidationException("Дата релиза — не раньше 1895.12.28.");
        } else if (film.getDuration().isNegative()) {
            log.debug("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        } else {
            films.put(film.getId(), film);
            log.info("Создан новый пользователь");
        }
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film){
        if(films.containsKey(film.getId())) {
            if (film.getName() == null || film.getName().isBlank()) {
                log.debug("Название не может быть пустым.");
                throw new ValidationException("Название не может быть пустым.");
            } else if (film.getDescription().length() > 200) {
                log.debug("Максимальная длина описания — 200 символов.");
                throw new ValidationException("Максимальная длина описания — 200 символов.");
            } else if (film.getReleaseDate().isBefore(upperLimit)) {
                log.debug("Дата релиза — не раньше 1895.12.28.");
                throw new ValidationException("Дата релиза — не раньше 1895.12.28.");
            } else if (film.getDuration().isNegative()) {
                log.debug("Продолжительность фильма должна быть положительной");
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            } else {
                films.put(film.getId(), film);
                log.info("Новый фильм добавлен");
            }
            return film;
        } else {
            log.info("Такого фильма не существует");
            return film;
        }
    }
}
