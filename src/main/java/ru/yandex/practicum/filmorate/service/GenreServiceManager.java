package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreServiceManager implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    @Override
    public Map<Integer, Genre> getGenresMap() {
        return genreStorage.getGenresMap();
    }

    @Override
    public Set<Genre> getGenresByFilmId(int filmId) {
        return genreStorage.getGenresByFilmId(filmId);
    }

    @Override
    public Map<Integer, Set<Genre>> getAllGenresOfAllFilms() {
        return genreStorage.getAllGenresOfAllFilms();
    }
}
