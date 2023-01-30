package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    private static boolean checkYear(Film film) {
        LocalDate checkDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isAfter(checkDate))
            return true;
        throw new ValidationException("Invalid film's release date");
    }

    private static boolean checkLike(Film film) {

        return true;
    }

    public static boolean valid(Film film) {
        return (checkYear(film));
    }
}
