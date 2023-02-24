package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();
    Film getFilmById(int id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilmById(int id);
    Film likeFilm(int filmId, int userId);
    Film deleteLikeFromFilm(int filmId, int userId);
    List<Film> getPopularFilms(int count);
    List <Film> searchFilms(String query, List<String> by);
    List<Film> getCommonFilms(int userId, int friendId);
}
