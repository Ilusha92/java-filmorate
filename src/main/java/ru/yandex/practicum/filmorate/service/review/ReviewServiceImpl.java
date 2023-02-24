package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final EventDbStorage eventDbStorage;

    @Override
    public Review add(Review review) {
        review = reviewStorage.add(review);
        reviewLikesStorage.add(review);
        eventDbStorage.saveEvent(review.getUserId(), EventTypes.REVIEW,
                OperationTypes.ADD, review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        review = reviewStorage.update(review);
        eventDbStorage.saveEvent(review.getUserId(), EventTypes.REVIEW,
                OperationTypes.UPDATE, review.getReviewId());
        return review;
    }

    @Override
    public void delete(long reviewId) {
        reviewStorage.delete(reviewId);
        reviewLikesStorage.delete(reviewId);
        eventDbStorage.saveEvent(Math.toIntExact(reviewId), EventTypes.REVIEW,
                OperationTypes.REMOVE, Math.toIntExact(reviewId));
    }

    @Override
    public Review getById(long reviewId) {
        Review review = reviewStorage.getById(reviewId);
        review.getLikes().putAll(reviewLikesStorage.getReviewLikesById(review.getReviewId()));
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, int limit) {
        List<Review> reviews = reviewStorage.getAllReviewsByFilmId(filmId, limit);
        return getReviewWithLikes(reviews);
    }

    @Override
    public List<Review> getAllReviews() {
        List<Review> reviews = reviewStorage.getAllReviews();
        return getReviewWithLikes(reviews);
    }

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
        review = reviewStorage.updateUseful(review);
        reviewLikesStorage.update(review);
    }

    private List<Review> getReviewWithLikes(List<Review> reviews) {
        if (reviews.size() > 0) {
            Map<Long, Boolean> reviewsLikes = reviewLikesStorage.getAllReviewLikes();
            if (reviewsLikes.size() > 0) {
                reviews.forEach(review -> reviewsLikes.forEach((userId, like) -> {
                    if (review.getUserId().equals(userId)) review.getLikes().put(userId, like);
                }));
            }
        }
        return reviews;
    }
}