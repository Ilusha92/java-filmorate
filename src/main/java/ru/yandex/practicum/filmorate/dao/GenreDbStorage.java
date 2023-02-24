package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet allGenres = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        while (allGenres.next()) {
            Genre genre = new Genre(allGenres.getInt("genreId"), allGenres.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE genreId = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("genreId"),
                    genreRows.getString("name"));
            return genre;
        } else {
            throw new GenreNotFoundException("Жанра с таким id нет в базе!");
        }
    }

    @Override
    public Set<Genre> getGenresByFilmId(int filmId) {
        Set<Genre> filmGenres = new HashSet<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_GENRE as fg " +
                "LEFT JOIN GENRE as g " +
                "ON fg.GENREID = g.GENREID " +
                "WHERE filmId = ?", filmId);
        while (rs.next()) {
            filmGenres.add(new Genre(rs.getInt("genreId"), rs.getString("name")));
        }
        return filmGenres;
    }

    @Override
    public Map<Integer, String> getGenreIdNamesMap() {
        Map<Integer, String> genreIdNamesMap = new HashMap<>();
        List<Genre> genres = getAllGenres();
        for (Genre genre : genres) {
            genreIdNamesMap.put(genre.getId(), genre.getName());
        }
        return genreIdNamesMap;
    }
}
