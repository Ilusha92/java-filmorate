package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
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
    private Set<Director> directors= new LinkedHashSet<>();

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Integer userId){
        likes.add(userId);
    }

    public void removeLike(Integer userId) {
        if (likes.contains(userId)){
            likes.remove(userId);
        } else {
            throw new NotFoundObjectException("Лайк от пользователя " + userId +
                    " этому фильму и так не был поставлен, удалять нечего");
        }
    }

    public void addGenre(Genre genre){
        genres.add(genre);
    }

    public Integer getLikesCount(){
        return likes.size();
    }
}