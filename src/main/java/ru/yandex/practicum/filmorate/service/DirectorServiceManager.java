package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorServiceManager implements DirectorService {
    private final DirectorStorage storage;

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
    public List<Film> findFilmsByDirectorId(Integer directorId) {
        return storage.findFilmsByDirectorId(directorId);
    }
}
