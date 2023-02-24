package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceManager implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventDbStorage eventDbStorage;

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> resultList  = filmStorage.getCommonFilms(userId, friendId);
        resultList.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return resultList;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public void deleteFilmById(int id) {
        filmStorage.deleteFilmById(id);
    }

    @Override
    public Film likeFilm(int filmId, int userId) {
        Film film = filmStorage.likeFilm(filmId, userId);
        eventDbStorage.saveEvent(userId, EventTypes.LIKE, OperationTypes.ADD, filmId);
        return film;
    }

    @Override
    public Film deleteLikeFromFilm(int filmId, int userId) {
        Film film = filmStorage.deleteLikeFromFilm(filmId, userId);
        eventDbStorage.saveEvent(userId, EventTypes.LIKE, OperationTypes.REMOVE, filmId);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        List<Film> resultList  = filmStorage.searchFilms(query, by);
        resultList.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return resultList;
    }
}