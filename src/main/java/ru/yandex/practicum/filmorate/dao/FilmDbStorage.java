package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("filmDBStorage")
public class FilmDbStorage implements FilmStorage {

    private static int filmId = 0;
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate FILMSTARTDATE = LocalDate.of(1895, 12, 28);


    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private int generateFilmId() {
        return ++filmId;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", new FilmMapper(jdbcTemplate, this));
        if (films != null) {
            films.forEach(film -> film.setDirectors(new HashSet<>(getDirectorByFilmId(film.getId()))));
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE filmId="+id;
        if (checkFilmInDb(id)){
            Film film = jdbcTemplate.query(sql, this::makeFilm);
            if (film != null) {
                film.setDirectors(new HashSet<>(getDirectorByFilmId(id)));
            }
            return film;
        }else{
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILMSTARTDATE)) {
            log.info("Не пройдена валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        }else {
            film.setId(generateFilmId());
            jdbcTemplate.update("INSERT INTO films(filmId, name, description, release_date, duration, mpaId) " +
                            "VALUES(?,?,?,?,?,?)",
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
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
        }
        return getFilmById(film.getId());
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
        checkFilmInDb(filmId);
        jdbcTemplate.update("DELETE FROM directorFilm WHERE filmId = ?", filmId);
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
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(jdbcTemplate, this));
        if (films != null) {
            films.forEach(film -> film.setDirectors(new HashSet<>(getDirectorByFilmId(film.getId()))));
        }
        return films;
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet allGenres = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        while(allGenres.next()){
            Genre genre = new Genre(allGenres.getInt("genreId"), allGenres.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        checkGenreInDb(id);
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE genreId = ?", id);
        if(genreRows.next()){
            Genre genre = new Genre(genreRows.getInt("genreId"),
                    genreRows.getString("name"));
            log.info("Жанр с id={}, это {}.", genre.getId(), genre.getName());
            return genre;
        }else{
            log.info("Жанра с таким id нет!");
            return null;
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet allMpas = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        while(allMpas.next()){
            Mpa mpa = new Mpa(allMpas.getInt("mpaId"), allMpas.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    public Mpa getMpaById(int id){
        checkMpaInDb(id);
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpaId = ?", id);
        if(mpaRows.next()){
            Mpa mpa = new Mpa(mpaRows.getInt("mpaId"), mpaRows.getString("name"));
            log.info("Рейтинг с id={}, это {}.", mpa.getId(), mpa.getName());
            return mpa;
        }else{
            log.info("Рейтинга с таким id нет!");
            return null;
        }
    }

    private boolean checkMpaInDb(int id){
        String sql = "SELECT mpaId FROM mpa";
        SqlRowSet getMpaFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getMpaFromDb.next()){
            ids.add(getMpaFromDb.getInt("mpaId"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new MpaNotFoundException("Рейтинга с таким id нет в базе!");
        }
    }

    private boolean checkFilmInDb(Integer id){
        String sql = "SELECT filmId FROM films";
        SqlRowSet getFilmFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getFilmFromDb.next()){
            ids.add(getFilmFromDb.getInt("filmId"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new NotFoundObjectException("Фильма с таким id нет в базе!");
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        if (rs.next()){
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"));
            film.setId(rs.getInt("filmId"));
            film.setMpa(getMpaById(rs.getInt("mpaId")));
            SqlRowSet getFilmGenres = jdbcTemplate.queryForRowSet("SELECT genreId FROM film_genre WHERE filmId=?", film.getId());
            while(getFilmGenres.next()){
                Genre genre = getGenreById(getFilmGenres.getInt("genreId"));
                film.addGenre(genre);
            }
            SqlRowSet getFilmLikes = jdbcTemplate.queryForRowSet("SELECT userId FROM likesList WHERE filmId = ?",
                    film.getId());
            while(getFilmLikes.next()){
                film.addLIke(getFilmLikes.getInt("userId"));
            }
            return film;
        }else{
            return null;
        }

    }

    private boolean checkGenreInDb(Integer id){
        String sql = "SELECT genreId FROM genre";
        SqlRowSet getGenreFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getGenreFromDb.next()){
            ids.add(getGenreFromDb.getInt("genreId"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new GenreNotFoundException("Жанра с таким id нет в базе!");
        }
    }

    private boolean checkUserInDb(Integer id){
        String sql = "SELECT userId FROM users";
        SqlRowSet getUsersFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getUsersFromDb.next()){
            ids.add(getUsersFromDb.getInt("userId"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new NotFoundObjectException("Пользователя с таким id нет в базе!");
        }


    }
    private List<Director> getDirectorByFilmId (Integer filmId) {
        String statement = "SELECT directorFilm.filmId, directors.directorId, directors.directorName " +
                "FROM directorFilm LEFT JOIN directors " +
                "ON directorFilm.directorId = directors.directorId WHERE directorFilm.filmId = ?";
        return jdbcTemplate.query(statement, new DirectorMapper(), filmId);
    }

}
