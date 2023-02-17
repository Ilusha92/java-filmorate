package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.time.LocalDate;
import java.util.*;


@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 0;

    private int generateFilmId() {
        return ++filmId;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundObjectException("Film with this " + filmId + " ID not found.");
        }
    }

    @Override
    public Film createFilm(Film film) {
        if (FilmValidator.valid(film)) {
            film.setId(generateFilmId());
            films.put(film.getId(), film);
            log.info("Film" + film.getId() + " created");
        } else {
            throw new ValidationException("Film with this " + film.getId() + " ID already created");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundObjectException("Not found this film");
        } else {
            films.remove(film.getId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public void deleteFilmById(int filmId) {
        films.remove(filmId);
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        return null;
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        return null;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return null;
    }

    @Override
    public Mpa getMpaById(int id) {
        return null;
    }

}