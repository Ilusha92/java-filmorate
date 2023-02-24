package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    Map<Integer, Genre> getGenresMap();

    Set<Genre> getGenresByFilmId(int filmId);
    Map<Integer, Set<Genre>> getAllGenresOfAllFilms();

}
