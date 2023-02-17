package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Map;

public interface ReviewLikesStorage {
    void add(Review review);
    void update(Review review);
    void delete(long reviewId);
    Map<Long, Boolean> getReviewLikesById(long reviewId);
    Map<Long, Boolean> getAllReviewLikes();
}