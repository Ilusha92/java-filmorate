package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorService {
    Director create (Director director);
    Director update (Director director);
    Director getById (Integer id);
    Director delete (Integer id);
    List<Director> getAll ();
    List<Film> getFilmsSortedByLikesOrYear (Integer id, String param);
}
