package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MpaServiceManager implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    @Override
    public Map<Integer, Mpa> getMpasMap() {
        return mpaStorage.getMpasMap();
    }
}
