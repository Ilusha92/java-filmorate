package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review add(Review review);
    Review update(Review review);
    void delete(long reviewId);
    Review getById(long reviewId);
    List<Review> getAllReviewsByFilmId(long filmId, int limit);
    List<Review> getAllReviews();
    Review addLike(long reviewId, long userId);
    Review deleteLike(long reviewId, long userId);
    Review addDislike(long reviewId, long userId);
    Review deleteDislike(long reviewId, long userId);
}