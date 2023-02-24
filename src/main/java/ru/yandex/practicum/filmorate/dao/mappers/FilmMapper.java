package ru.yandex.practicum.filmorate.dao.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmMapper implements ResultSetExtractor<List<Film>> {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaService mpaService;

    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Film> films = new ArrayList<>();
        while (rs.next()) {
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"));
            film.setId(rs.getInt("filmId"));
            film.setMpa(mpaService.getMpaById(rs.getInt("mpaId")));
            SqlRowSet getFilmGenres = jdbcTemplate.queryForRowSet("SELECT genreId FROM film_genre WHERE filmId = ?", film.getId());
//            while(getFilmGenres.next()){
//                Genre genre = genreStorage.getGenreById(getFilmGenres.getInt("genreId"));
//                film.addGenre(genre);
//            }
            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));

            SqlRowSet getFilmLikes = jdbcTemplate.queryForRowSet("SELECT userId FROM likesList WHERE filmId = ?",
                    film.getId());
            while(getFilmLikes.next()){
                film.addLike(getFilmLikes.getInt("userId"));
            }
            films.add(film);
        }
        return films;
    }
}