package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewService {
    Review addLike(long reviewId, long userId);
    Review deleteLike(long reviewId, long userId);
    Review addDislike(long reviewId, long userId);
    Review deleteDislike(long reviewId, long userId);
}