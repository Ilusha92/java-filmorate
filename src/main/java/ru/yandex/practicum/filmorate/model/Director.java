package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class Director {
    private Integer id;
    @NotNull
    private String directorName;
    private Set<Film> films;
}
