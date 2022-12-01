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

    private int generateFilmId() {
        filmId++;
        return filmId;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getReleaseDate().isAfter(upperLimit)) {
            film.setId(generateFilmId());
            films.put(film.getId(), film);
            log.info("Список фильмов пополнен.");
        }else {
            throw new ValidationException("Такого фильма нет в коллекции");
        }
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Такого фильма нет в коллекции");
        }
        return film;
    }
}

