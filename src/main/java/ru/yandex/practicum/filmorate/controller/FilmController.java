package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int filmId = 0;
    private final LocalDate upperLimit = LocalDate.parse("1895-12-28");

    public int generateFilmId() {
        filmId++;
        return filmId;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (validate(film)) {
            log.info("Фильм не прошел валидацию.");
            throw new ValidationException("Фильм не прошел валидацию.");
        }
        film.setId(generateFilmId());
        films.put(film.getId(), film);
        log.info("Список фильмов пополнен.");
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            if (!validate(film)) {
                films.remove(film.getId());
                films.put(film.getId(), film);
            } else {
                log.info("Фильм не прошел валидацию.");
                throw new ValidationException("Фильм не прошел валидацию.");
            }
        }else {
            throw new ValidationException("Не валид.");
        }
        return film;
    }


    public boolean validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Название фильма не указано");
            throw new ValidationException("Название фильма не указано.");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Описание фильма больше 200 символов");
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(upperLimit)) {
            log.debug("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <=0) {
            log.debug("Продолжительность фильма отрицательная");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной.");
        }
        return false;
    }
}
//
//    @PutMapping("/films")
//    public Film updateFilm(@RequestBody Film film){
//        if(films.containsKey(film.getId())) {
//            if (film.getName() == null || film.getName().isBlank()) {
//                log.debug("Название не может быть пустым.");
//                throw new ValidationException("Название не может быть пустым.");
//            } else if (film.getDescription().length() > 200) {
//                log.debug("Максимальная длина описания — 200 символов.");
//                throw new ValidationException("Максимальная длина описания — 200 символов.");
//            } else if (film.getReleaseDate().isBefore(upperLimit)) {
//                log.debug("Дата релиза — не раньше 1895.12.28.");
//                throw new ValidationException("Дата релиза — не раньше 1895.12.28.");
//            } else if (film.getDuration().isNegative()) {
//                log.debug("Продолжительность фильма должна быть положительной");
//                throw new ValidationException("Продолжительность фильма должна быть положительной");
//            } else {
//                films.put(film.getId(), film);
//                log.info("Новый фильм добавлен");
//            }
//            return film;
//        } else {
//            log.info("Такого фильма не существует");
//            return film;
//        }
//    }

