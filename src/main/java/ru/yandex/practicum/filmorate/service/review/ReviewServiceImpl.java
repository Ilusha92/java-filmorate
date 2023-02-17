package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.ValidationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;

    @Override
    public Review addLike(long reviewId, long userId) {
        Review review = reviewStorage.getById(reviewId);
        Map<Long, Boolean> reviewLikes = review.getLikes();

        if (reviewLikes.containsKey(userId) && reviewLikes.get(userId).equals(true)) {
            throw new ValidationException("Этот пользователь уже поставил лайк.");
        }
        reviewLikes.put(userId, true);
        updateReviewLikes(review, reviewLikes);
        return review;
    }

    @Override
    public Review deleteLike(long reviewId, long userId) {
        Review review = reviewStorage.getById(reviewId);
        Map<Long, Boolean> reviewLikes = review.getLikes();

        if (!reviewLikes.containsKey(userId) && reviewLikes.get(userId).equals(false)) {
            throw new ValidationException("Этот пользователь не ставил лайк.");
        }
        reviewLikes.remove(userId);
        updateReviewLikes(review, reviewLikes);
        return review;
    }

    @Override
    public Review addDislike(long reviewId, long userId) {
        Review review = reviewStorage.getById(reviewId);
        Map<Long, Boolean> reviewLikes = review.getLikes();

        if (reviewLikes.containsKey(userId) && reviewLikes.get(userId).equals(false)) {
            throw new ValidationException("Этот пользователь уже поставил дизлайк.");
        }
        reviewLikes.put(userId, false);
        updateReviewLikes(review, reviewLikes);
        return review;
    }

    @Override
    public Review deleteDislike(long reviewId, long userId) {
        Review review = reviewStorage.getById(reviewId);
        Map<Long, Boolean> reviewLikes = review.getLikes();

        if (!reviewLikes.containsKey(userId) && reviewLikes.get(userId).equals(true)) {
            throw new ValidationException("Этот пользователь уже поставил дизлайк.");
        }
        reviewLikes.remove(userId);
        updateReviewLikes(review, reviewLikes);
        return review;
    }

    private void setNewUseful(Review review) {
        int newUseful;
        Map<Long, Boolean> reviewLikes = review.getLikes();
        AtomicInteger likes = new AtomicInteger();
        reviewLikes.values().forEach(like -> {
            if (like) likes.getAndIncrement();
            if (!like) likes.decrementAndGet();
        });
        newUseful = likes.intValue();
        review.setUseful(newUseful);
    }

    private void updateReviewLikes(Review review, Map<Long, Boolean> reviewLikes) {
        review.getLikes().putAll(reviewLikes);
        setNewUseful(review);
        reviewStorage.updateUseful(review);
    }
}