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
            return new Genre(genreRows.getInt("genreId"),
                    genreRows.getString("name"));
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
    public Map<Integer, Genre> getGenresMap() {
        Map<Integer, Genre> genreIdNamesMap = new HashMap<>();
        List<Genre> genres = getAllGenres();
        for (Genre genre : genres) {
            genreIdNamesMap.put(genre.getId(), genre);
        }
        return genreIdNamesMap;
    }

    public Map<Integer, Set<Genre>> getAllGenresOfAllFilms() {
        Map<Integer, Set<Genre>> genresOfAllFilms = new HashMap<>();
        String sql = "SELECT filmId, genreId FROM FILM_GENRE";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        Map<Integer, Genre> allGenres = getGenresMap();
        while (rs.next()) {
            Integer filmId = rs.getInt("filmId");
            Integer genreId = rs.getInt("genreId");
            if (genresOfAllFilms.containsKey(filmId)) {
                genresOfAllFilms.get(filmId).add(allGenres.get(genreId));
                continue;
            }
            genresOfAllFilms.put(filmId, new HashSet<>() {{
                add(allGenres.get(genreId));
            }});
        }
        return genresOfAllFilms;
    }
}
