package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();
    Film getFilmById(int filmId);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void deleteFilmById(int filmId);

}
