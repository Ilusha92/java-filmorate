package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;
    @Positive(message = "Duration is required and must be greater than 0")
    private int duration;
    private Set<Integer> likes = new LinkedHashSet<>();
    private Set<Genre> genres = new LinkedHashSet<>();
    private Mpa mpa;
    private Director director;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLIke(Integer userId){
        likes.add(userId);
    }

    public void removeLike(Integer userId){
        if (likes.contains(userId)){
            likes.remove(userId);
        }else{
            throw new NotFoundObjectException("Лайк от пользователя "+userId+" этому фильму и так не был поставлен, " +
                    "удалять нечего");
        }
    }

    public void addGenre(Genre genre){
        genres.add(genre);
    }

}
