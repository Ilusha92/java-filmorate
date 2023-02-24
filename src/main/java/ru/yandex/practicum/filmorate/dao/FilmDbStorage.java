package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component("filmDBStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate FILM_START_DATE = LocalDate.of(1895, 12, 28);
    private final FilmMapper filmMapper;

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", filmMapper);
        if (films != null) {
            films.forEach(film -> film.setDirectors(new HashSet<>(getDirectorByFilmId(film.getId()))));
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT DISTINCT(FILMID), NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPAID " +
                "FROM FILMS as f JOIN " +
                "(SELECT A.FILMID as FI, A.USERID as AU, B.USERID as BU " +
                "FROM LIKESLIST A, LIKESLIST B " +
                "WHERE A.FILMID = B.FILMID AND A.USERID <> B.USERID) as common " +
                "ON f.FILMID = common.FI " +
                "WHERE (AU = " + userId + " AND BU = " + friendId + ")";
        return jdbcTemplate.query(sql, filmMapper);
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE filmId="+id;
        if (checkFilmInDb(id)){
            Film film = jdbcTemplate.query(sql, filmMapper).get(0);
            if (film != null) {
                film.setDirectors(new HashSet<>(getDirectorByFilmId(id)));
            }
            return film;
        } else {
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILM_START_DATE)) {
            log.info("Не пройдена валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        }
        String sql = "INSERT INTO FILMS (MPAID, NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"filmId"});
            ps.setInt(1, film.getMpa().getId());
            ps.setString(2, film.getName());
            ps.setString(3, film.getDescription());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getDuration());
            return ps;
        }, keyHolder);
        int generatedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(generatedId);
        setGenres(film);
        setDirectors(film);
        return getFilmById(generatedId);
    }

    private void setGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        int filmId = film.getId();
        int commaAndSpace = 2;
        if (genres.size() > 0) {
            StringBuilder sqlGenre = new StringBuilder("INSERT INTO film_genre(filmId, genreId) VALUES ");
            genres.forEach(genre -> sqlGenre.append(String.format("(%d, %d), ", filmId, genre.getId())));
            sqlGenre.setLength(sqlGenre.length() - commaAndSpace);
            jdbcTemplate.update(sqlGenre.toString());
        }
    }

    private void setDirectors(Film film) {
        int commaAndSpace = 2;
        int filmId = film.getId();
        jdbcTemplate.update("DELETE FROM directorFilm WHERE filmId = ?", film.getId());
        Set<Director> directors = film.getDirectors();
        if (directors != null && directors.size() > 0) {
            StringBuilder sqlDirectors = new StringBuilder("INSERT INTO directorFilm (filmId, directorId) VALUES ");
            directors.forEach(director -> sqlDirectors.append(String.format("(%d, %d), ", filmId, director.getId())));
            sqlDirectors.setLength(sqlDirectors.length() - commaAndSpace);
            jdbcTemplate.update(sqlDirectors.toString());
        }
    }

    private void setLikes(Film film) {
        int commaAndSpace = 2;
        int filmId = film.getId();
        Set<Integer> likes = film.getLikes();
        if (likes.size() > 0) {
            StringBuilder sql = new StringBuilder("INSERT INTO likesList VALUES ");
            likes.forEach(userId -> sql.append(String.format("(%d, %d)", filmId, userId)));
            sql.setLength(sql.length() - commaAndSpace);
            jdbcTemplate.update(sql.toString());
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if(checkFilmInDb(film.getId())) {
            jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, " +
                            "mpaId=? WHERE filmId=?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            jdbcTemplate.update("DELETE FROM likesList WHERE filmId=?", film.getId());
            jdbcTemplate.update("DELETE FROM film_genre WHERE filmId=?", film.getId());
            setLikes(film);
            setGenres(film);
            setDirectors(film);
        }
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilmById(int filmId) {
        if(checkFilmInDb(filmId)) {
            jdbcTemplate.update("DELETE FROM films where filmId = ?", filmId);
        } else {
            throw new NotFoundObjectException("Фильм с filmId " + filmId + " не был удален.");
        }
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        jdbcTemplate.update("INSERT INTO likesList VALUES (?,?)", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        jdbcTemplate.update("DELETE FROM likesList WHERE filmId=? AND userId=?", filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, count(fl.userId) AS likes FROM films AS f LEFT JOIN likesList AS fl " +
                "ON f.filmId=fl.filmId GROUP BY f.filmId ORDER BY likes DESC LIMIT "+count;
        List<Film> films = jdbcTemplate.query(sql, filmMapper);
        if (films != null) {
            films.forEach(film -> film.setDirectors(new HashSet<>(getDirectorByFilmId(film.getId()))));
        }
        return films;
    }

    private boolean checkFilmInDb(Integer id) {
        String sql = "SELECT filmId FROM films";
        SqlRowSet getFilmFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getFilmFromDb.next()) {
            ids.add(getFilmFromDb.getInt("filmId"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new NotFoundObjectException("Фильма с таким id нет в базе!");
        }
    }

    private boolean checkUserInDb(Integer id) {
        String sql = "SELECT userId FROM users";
        SqlRowSet getUsersFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getUsersFromDb.next()) {
            ids.add(getUsersFromDb.getInt("userId"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new NotFoundObjectException("Пользователя с таким id нет в базе!");
        }
    }

    private List<Director> getDirectorByFilmId (Integer filmId) {
        String statement = "SELECT df.filmId, d.directorId, d.directorName " +
                "FROM directorFilm AS df LEFT JOIN directors AS d " +
                "ON df.directorId = d.directorId WHERE df.filmId = ?";
        return jdbcTemplate.query(statement, new DirectorMapper(), filmId);
    }

    public List<Film> searchFilms(String query1, List<String> by) {
        List<Film> searchResultFilms = new ArrayList<>();
        if (by.size() == 1) {
            if (by.get(0).equalsIgnoreCase("title")) {
                searchResultFilms.addAll(Objects.requireNonNull(jdbcTemplate.query(
                        "SELECT * FROM films WHERE lower(name) LIKE ?", filmMapper, "%" + query1.toLowerCase() + "%")));
            }
            if (by.get(0).equalsIgnoreCase("director")) {
                searchByDirector(query1,searchResultFilms);
            }
        }
        if (by.size() == 2) {
            searchResultFilms.addAll(Objects.requireNonNull(jdbcTemplate.query(
                    "SELECT * FROM films WHERE lower(name) LIKE ?", filmMapper, "%" + query1.toLowerCase() + "%")));
            searchByDirector(query1,searchResultFilms);
        }
        return searchResultFilms;
    }

    private void searchByDirector(String query1, List<Film> searchResultFilms) {
        SqlRowSet searchByDirector = jdbcTemplate.queryForRowSet(
                "SELECT df.* FROM directorFilm as df " +
                        "INNER JOIN directors as d ON df.directorId = d.directorId " +
                        "WHERE lower(d.directorName) LIKE lower(CONCAT('%',?,'%'))", query1);
        while (searchByDirector.next()) {
            Integer filmId1 = searchByDirector.getInt("filmId");
            searchResultFilms.add(getFilmById(filmId1));
        }
    }
}