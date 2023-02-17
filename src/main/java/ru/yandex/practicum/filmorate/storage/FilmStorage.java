package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();
    Film getFilmById(int filmId);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilmById(int filmId);
    Film likeFilm(int filmId, int userId);
    Film deleteLikeFromFilm(int filmId, int userId);
    List<Film> getPopularFilms(int count);
    List<Genre> getAllGenres();
    Genre getGenreById(int id);
    List<Mpa> getAllMpa();
    Mpa getMpaById(int id);
}
