package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll ();
    Director create (Director director);
    Director update (Director director);
    Director findById (Integer id);
    Director delete (Integer id);
    List<Film> findFilmsByDirectorId (Integer directorId);

}
