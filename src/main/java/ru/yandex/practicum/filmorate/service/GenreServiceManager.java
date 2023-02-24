package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GenreServiceManager implements GenreService {

    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreDbStorage.getGenreById(id);
    }

    @Override
    public Map<Integer, String> getGenreIdNamesMap() {
        return genreDbStorage.getGenreIdNamesMap();
    }
}
