package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") int userId, @RequestParam("friendId") int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Creating Film {}", film.getName());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Updating Film {}", film.getName());
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        log.info("Deleting Film {}", id);
        filmService.deleteFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(
            @PathVariable int id,
            @PathVariable int userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFromFilm(
            @PathVariable int id,
            @PathVariable int userId) {
        return filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping ("director/{directorId}")
    public List<Film> getFilmsByDirectorId (@PathVariable Integer directorId,
                                            @RequestParam (value = "sortBy")String param) {
      return directorService.getFilmsSortedByLikesOrYear(directorId, param);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam @NotNull @NotBlank String query,
                                  @RequestParam @NotNull @NotBlank List<String> by) {
        return filmService.searchFilms(query, by);
    }
}

