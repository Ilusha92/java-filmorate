package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceManager implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final EventDbStorage eventDbStorage;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> resultList  = filmStorage.getCommonFilms(userId, friendId);
        resultList.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return setGenresOfFilmList(resultList);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        return setGenresOfFilmList(films);
    }

    private List<Film> setGenresOfFilmList(List<Film> films) {
        Map<Integer, Set<Genre>> allGenresOfAllFilms = genreService.getAllGenresOfAllFilms();
        for (Film film : films) {
            film.setGenres(allGenresOfAllFilms.get(film.getId()));
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        film.setGenres(genreService.getGenresByFilmId(filmId));

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        filmStorage.createFilm(film);
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        film.setGenres(genreService.getGenresByFilmId(film.getId()));
        return film;
    }

    @Override
    public void deleteFilmById(int id) {
        filmStorage.deleteFilmById(id);
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        userService.checkUserInDb(userId);
        Film film = filmStorage.likeFilm(filmId, userId);
        eventDbStorage.saveEvent(userId, EventTypes.LIKE, OperationTypes.ADD, filmId);
        return film;
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        userService.checkUserInDb(userId);
        Film film = filmStorage.deleteLikeFromFilm(filmId, userId);
        film.setGenres(genreService.getGenresByFilmId(filmId));
        eventDbStorage.saveEvent(userId, EventTypes.LIKE, OperationTypes.REMOVE, filmId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return setGenresOfFilmList(filmStorage.getPopularFilms(count));
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        List<Film> resultList  = filmStorage.searchFilms(query, by);
        resultList.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return setGenresOfFilmList(resultList);
    }

    @Override
    public List<Film> getFilmsSortedByLikesOrYear(Integer directorId, String param) {
        List<Film> filmsByDirectorId = directorService.findFilmsByDirectorId(directorId);
        setGenresOfFilmList(filmsByDirectorId);

        if ("year".equalsIgnoreCase(param)) {
            return filmsByDirectorId
                    .stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
        }
        else  if ("likes".equalsIgnoreCase(param)) {
            return filmsByDirectorId.stream().sorted(Comparator.comparing(film -> film.getLikes().size())).collect(Collectors.toList());
        }

        return filmsByDirectorId;
    }
}