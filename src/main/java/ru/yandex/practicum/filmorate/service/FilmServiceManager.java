package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceManager implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilmById(int id) {
        filmStorage.deleteFilmById(id);
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null) {
            throw new NotFoundObjectException("Нет такого фильма");
        }

        if (user == null) {
            throw new NotFoundObjectException("Нет такого юзера");
        }

       film.addLIke(userId);


        return filmStorage.updateFilm(film);
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null) {
            throw new NotFoundObjectException("Нет такого фильма");
        }

        if (user == null) {
            throw new NotFoundObjectException("Нет такого юзера");
        }
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundObjectException("Нет лайка от такого юзера");
        }

        film.removeLike(userId);

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        Collection<Film> films = filmStorage.getAllFilms();
        return films
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}