package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    public DirectorDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public List<Director> findAll() {
        String statement = "SELECT * FROM directors";
        List<Director> directors = jdbcTemplate.query(statement, new DirectorMapper());
        directors.forEach(director -> director.setFilms(new HashSet<>(findFilmsByDirectorId(director.getId()))));
        return directors;
    }

    @Override
    public Director create(Director director) {
        String statement = "INSERT INTO directors (directorName) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getDirectorName());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            updateFilmsForDirector(keyHolder.getKey().intValue(), director.getFilms());
            return findById(keyHolder.getKey().intValue());
        }
        return null;
    }

    @Override
    public Director update(Director director) {
        if (director.getId() == null) {
            throw new NotFoundObjectException("Director id is null");
        }
        if (! isExists(director.getId())) {
            throw new NotFoundObjectException("Director with " + director.getId() + " not found");
        }
        String statement = "UPDATE directors SET directorName = ? WHERE directorId = ?";
        jdbcTemplate.update(statement, director.getDirectorName(), director.getId());
        updateFilmsForDirector(director.getId(), director.getFilms());
        return findById(director.getId());
    }

    @Override
    public Director findById(Integer id) {
        if (! isExists(id)) {
            throw new NotFoundObjectException("Director with " + id + " not found");
        }
        String statement = "SELECT * FROM directors WHERE directorID = ?";
        Director director = jdbcTemplate.queryForObject(statement, new DirectorMapper(), id);
        if (director == null) {
            throw new NotFoundObjectException("Director with " + id + " not found");
        }
        director.setFilms(new HashSet<>(findFilmsByDirectorId(id)));
        return director;
    }

    @Override
    public Director delete(Integer id) {
        Director director = findById(id);
        String statement = "DELETE FROM directors WHERE directorID = ?";
        jdbcTemplate.update(statement, id);
        deleteDirectorFromDirectorFilm(id);
        return director;
    }

    public List<Film> findFilmsByDirectorId (Integer directorId) {
       if (! isExists(directorId)) {
           throw new NotFoundObjectException("Director with " + directorId + " not found");
       }
        String statement = "SELECT directorId,films.* FROM directorFilm LEFT JOIN films ON directorFilm.filmId = films.filmId WHERE directorId = ?";
        return jdbcTemplate.query(statement, new FilmMapper(jdbcTemplate, filmDbStorage), directorId);
    }

    public void deleteDirectorFromDirectorFilm (Integer id) {
        String statement = "DELETE FROM directorFilm WHERE directorId = ?";
        jdbcTemplate.update(statement,id);
    }

    private void updateFilmsForDirector (Integer directorId, Set<Film> films) {
        String statement = "DELETE FROM directorFilm WHERE directorId = ?";
        jdbcTemplate.update(statement,directorId);
        statement = "INSERT INTO directorFilm (directorId, filmId) VALUES (?, ?)";
        for (Film film : films) {
            jdbcTemplate.update(statement, directorId, film.getId());
        }
    }

    public boolean isExists(Integer id) {
        String s   = "SELECT COUNT(*) FROM directors WHERE directorId=?";
        Long   obj = jdbcTemplate.queryForObject(s, Long.class, id);
        if (obj != null) {
            return obj != 0;
        } else {
            return false;
        }
    }
}
