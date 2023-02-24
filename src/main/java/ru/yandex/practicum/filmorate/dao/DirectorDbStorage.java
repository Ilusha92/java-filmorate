package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public List<Director> findAll() {
        String statement = "SELECT * FROM directors";
        List<Director> directors = jdbcTemplate.query(statement, new DirectorMapper());
        directors.forEach(director -> director.setFilms(new HashSet<>(findFilmsByDirectorId(director.getId()))));
        return directors;
    }

    @Override
    public Director create(Director director) {
        if(director.getName() == null || director.getName().isBlank()) {
            log.warn("Имя директора отсутствует");
            throw  new ValidationException("Name is required");
        }
        String statement = "INSERT INTO directors (directorName) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
           if (director.getFilms() != null) {
               updateFilmsForDirector(keyHolder.getKey().intValue(), director.getFilms());
           }
            return findById(keyHolder.getKey().intValue());
        }
        return null;
    }

    @Override
    public Director update(Director director) {
        if (director.getId() == null) {
            log.warn("Обьект директора = null");
            throw new NotFoundObjectException("Director id is null");
        }
        if (! isExists(director.getId())) {
            throw new NotFoundObjectException("Director with " + director.getId() + " not found");
        }
        if(director.getName() == null || director.getName().isBlank()) {
            throw  new ValidationException("Name is required");
        }
        String statement = "UPDATE directors SET directorName = ? WHERE directorId = ?";
        jdbcTemplate.update(statement, director.getName(), director.getId());
        if (director.getFilms() != null) {
            updateFilmsForDirector(director.getId(), director.getFilms());
        }

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
        return director;
    }

    public List<Film> findFilmsByDirectorId (Integer directorId) {
       if (! isExists(directorId)) {
           throw new NotFoundObjectException("Director with " + directorId + " not found");
       }
        String statement = "SELECT directorId,films.* FROM directorFilm " +
                "LEFT JOIN films ON directorFilm.filmId = films.filmId WHERE directorId = ?";
        List<Film> films = jdbcTemplate.query(statement, filmMapper, directorId);
        if (films != null) {
            films.forEach(film -> film.setDirectors(new HashSet<>(findDirectorsByFilmId(film.getId()))));
        }
        return films;
    }

    private void updateFilmsForDirector (Integer directorId, Set<Film> films) {
        String deleteStatement = "DELETE FROM directorFilm WHERE directorId = ?";
        int commaAndSpace = 2;
        jdbcTemplate.update(deleteStatement, directorId);
        StringBuilder updateStatment = new StringBuilder("INSERT INTO directorFilm (directorId, filmId) VALUES ");
        films.forEach(film -> updateStatment.append(String.format("(%d, %d), ", directorId, film.getId())));
        updateStatment.setLength(updateStatment.length() - commaAndSpace);
        jdbcTemplate.update(updateStatment.toString());
    }

    private boolean isExists(Integer id) {
        String s   = "SELECT COUNT(*) FROM directors WHERE directorId=?";
        Long   obj = jdbcTemplate.queryForObject(s, Long.class, id);
        if (obj != null) {
            return obj != 0;
        } else {
            return false;
        }
    }

    private List<Director> findDirectorsByFilmId (Integer filmId) {
        String statement = "SELECT df.filmId, d.directorId, d.directorName FROM directorFilm AS dF " +
                "LEFT JOIN directors AS d ON df.directorId = d.directorId WHERE df.filmId = ?";
        return jdbcTemplate.query(statement, new DirectorMapper(), filmId);
    }
}