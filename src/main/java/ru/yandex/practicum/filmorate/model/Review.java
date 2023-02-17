package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class Review {
    private long id;
    @NotNull(message = "Пустое описание рецензии")
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
    private int useful;
    private final Map<Long, Boolean> likes = new HashMap<>(); // User ID - true/false for like/dislike
}