package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll ();
    Director create (Director director);
    Director update (Director director);
    Director findById (Integer id);
    Director delete (Integer id);


}
