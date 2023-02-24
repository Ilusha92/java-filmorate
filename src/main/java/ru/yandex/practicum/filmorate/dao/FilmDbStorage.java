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


@Slf4j
@Component("filmDBStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate FILM_START_DATE = LocalDate.of(1895, 12, 28);
    private final FilmMapper filmMapper;
    private final MpaService mpaService;

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

        if (film.getGenres().size() > 0) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(filmId, genreId) VALUES(?,?)",
                        film.getId(),
                        genre.getId());
            }
        }

        jdbcTemplate.update("DELETE FROM directorFilm WHERE filmId = ?", film.getId());
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(d-> jdbcTemplate.update("INSERT INTO directorFilm (filmId, directorId) VALUES (?, ?)", film.getId(), d.getId()));
        }

        return getFilmById(generatedId);
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
            for (Integer userId : film.getLikes()) {
                jdbcTemplate.update("INSERT INTO likesList VALUES(?,?)", film.getId(), userId);
            }
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(filmId, genreId) " +
                        "VALUES(?,?)", film.getId(), genre.getId());
            }
            jdbcTemplate.update("DELETE FROM directorFilm WHERE filmId = ?", film.getId());
            if (film.getDirectors() != null) {
                film.getDirectors().forEach(d-> jdbcTemplate.update("INSERT INTO directorFilm (filmId, directorId) VALUES (?, ?)", film.getId(), d.getId()));
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilmById(int filmId) {
        if(checkFilmInDb(filmId)) {
            jdbcTemplate.update("DELETE FROM film_genre where filmId = ?", filmId);
            jdbcTemplate.update("DELETE FROM likesList where filmId = ?", filmId);
            jdbcTemplate.update("DELETE FROM films where filmId = ?", filmId);
            log.info("Фильм с filmId " + filmId + " был удален.");
            jdbcTemplate.update("DELETE FROM directorFilm WHERE filmId = ?", filmId);
        } else {
            log.info("Фильм с filmId " + filmId + " не был удален.");
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
        String statement = "SELECT directorFilm.filmId, directors.directorId, directors.directorName " +
                "FROM directorFilm LEFT JOIN directors " +
                "ON directorFilm.directorId = directors.directorId WHERE directorFilm.filmId = ?";
        return jdbcTemplate.query(statement, new DirectorMapper(), filmId);
    }

    public List<Film> searchFilms(String query1, List<String> by) {
        List<Film> films1 = new ArrayList<>();
        if (by.size() == 1) {
            if (by.get(0).equalsIgnoreCase("title")) {
                searchByTitle(query1, films1);
                }
            if (by.get(0).equalsIgnoreCase("director")) {
                searchByDirector(query1, films1);
            }
        }
        if (by.size() == 2) {
            searchByTitle(query1, films1);
            searchByDirector(query1, films1);
        }
        return films1;
    }

    private void searchByTitle(String query1, List<Film> films1) {
        SqlRowSet searchByTitle = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE lower(name) LIKE lower(CONCAT('%',?,'%'))", query1);
        while (searchByTitle.next()) {
            Film film = new Film(searchByTitle.getString("name"),
                    searchByTitle.getString("description"),
                    searchByTitle.getDate("release_date").toLocalDate(),
                    searchByTitle.getInt("duration"));
            film.setId(searchByTitle.getInt("filmId"));
            film.setMpa(mpaService.getMpaById(searchByTitle.getInt("mpaId")));
            //необходимо извабивиться от использования данного сервиса именно здесь
            films1.add(film);
        }
    }

    private void searchByDirector(String query1, List<Film> films1) {
        SqlRowSet searchByDirector = jdbcTemplate.queryForRowSet(
                "SELECT df.* FROM directorFilm as df " +
                        "INNER JOIN directors as d ON df.directorId = d.directorId " +
                        "WHERE lower(d.directorName) LIKE lower(CONCAT('%',?,'%'))", query1);
        while (searchByDirector.next()) {
            Integer filmId1 = searchByDirector.getInt("filmId");
            films1.add(getFilmById(filmId1));
        }
    }
}
