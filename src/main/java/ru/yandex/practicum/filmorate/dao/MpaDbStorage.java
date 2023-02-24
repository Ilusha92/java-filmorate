package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet allMpas = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        while (allMpas.next()) {
            Mpa mpa = new Mpa(allMpas.getInt("mpaId"), allMpas.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    @Override
    public Mpa getMpaById(int id) {
        checkMpaInDb(id);
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpaId = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("mpaId"), mpaRows.getString("name"));
            log.info("Рейтинг с id={}, это {}.", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("Рейтинга с таким id нет!");
            return null;
        }
    }

    @Override
    public Map<Integer, String> getMpaIdNamesMap() {
        Map<Integer, String> mpaIdNamesMap = new HashMap<>();
        List<Mpa> mpas = getAllMpa();
        for (Mpa mpa : mpas) {
            mpaIdNamesMap.put(mpa.getId(), mpa.getName());
        }
        return mpaIdNamesMap;
    }

    private boolean checkMpaInDb(int id) {
        String sql = "SELECT mpaId FROM mpa";
        SqlRowSet getMpaFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getMpaFromDb.next()){
            ids.add(getMpaFromDb.getInt("mpaId"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new MpaNotFoundException("Рейтинга с таким id нет в базе!");
        }
    }

}
