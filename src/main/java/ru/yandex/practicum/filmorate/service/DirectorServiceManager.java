package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DirectorServiceManager implements DirectorService {
    private DirectorStorage storage;

    @Autowired
    public DirectorServiceManager(DirectorStorage storage) {
        this.storage = storage;
    }

    @Override
    public Director create(Director director) {
        return storage.create(director);
    }

    @Override
    public Director update(Director director) {
        return storage.update(director);
    }

    @Override
    public Director getById(Integer id) {
        return storage.findById(id);
    }

    @Override
    public Director delete(Integer id) {
        return storage.delete(id);
    }

    @Override
    public List<Director> getAll() {
        return storage.findAll();
    }

    @Override
    public List<Film> getFilmsSortedByLikesOrYear(Integer id, String param) {
        List<Film> filmsByDirectorId = storage.findFilmsByDirectorId(id);

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
