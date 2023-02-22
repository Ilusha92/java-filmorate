package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Set;

@Data
public class Director {
    private Integer id;
    private String name;
    private Set<Film> films;
}
