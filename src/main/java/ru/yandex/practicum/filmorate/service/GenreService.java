package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    Map<Integer, String> getGenreIdNamesMap();
}
