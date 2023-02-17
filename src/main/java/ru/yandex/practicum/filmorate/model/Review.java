package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class Review {
    private long reviewId;
    @NotNull(message = "Пустое описание рецензии")
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private int useful;
    private final Map<Long, Boolean> likes = new HashMap<>(); // User ID - true/false for like/dislike
}