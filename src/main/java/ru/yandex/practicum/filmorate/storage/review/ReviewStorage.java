package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);
    Review update(Review review);
    Review delete(Review review);
    Review getById(long reviewId);
    List<Review> getAllReviewsByFilmId(long filmId, int limit);
    List<Review> getReviewsWithLimit(int limit);
    List<Review> getAllReviews();
}