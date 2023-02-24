package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;

public interface MpaStorage {
    List<Mpa> getAllMpa();

    Mpa getMpaById(int id);

    Map<Integer, String> getMpaIdNamesMap();
}
