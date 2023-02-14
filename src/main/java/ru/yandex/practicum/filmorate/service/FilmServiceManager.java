package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmServiceManager implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceManager(@Qualifier("filmDBStorage") FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getAllFilms() {
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
       return filmStorage.likeFilm(filmId, userId);
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        return filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    @Override
    public Mpa getMpaById(int id) {
        return filmStorage.getMpaById(id);
    }



}