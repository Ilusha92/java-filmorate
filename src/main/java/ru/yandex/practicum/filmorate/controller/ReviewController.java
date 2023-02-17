package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService service;
    private final ReviewStorage storage;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        try {
            Review review = storage.getById(reviewId);
            log.info("Обзор с ID #{} пользователя с ID #{} передан", review.getReviewId(), review.getUserId());
            return review;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PostMapping
    public Review postReview(@RequestBody @Valid Review review) {
        try {
            review = storage.add(review);
            log.info("Отзыв с ID #{} добавлен.", review.getReviewId());
            return review;
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Review putReview(@RequestBody @Valid Review review) {
        try {
            review = storage.update(review);
            log.info("Отзыв с ID #{} обновлен.", review.getReviewId());
            return review;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") long reviewId) {
        try {
            storage.delete(reviewId);
            log.info("Отзыв с ID #{} удален.", reviewId);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        try {
            List<Review> reviews;
            if (filmId == null) {
                reviews = storage.getAllReviews();
                log.info("Передан список всех отзывов в размере {}", reviews.size());
                return reviews;
            }
            reviews = storage.getAllReviewsByFilmId(filmId, count);
            log.info("Список отзывов в размере {} фильма с ID {}", count, filmId);
            return reviews;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        try {
            Review review = service.addLike(reviewId, userId);
            log.info("Лайк отзыв с ID #{} пользователя c ID #{} добавлен. Количество лайков отзыва {}",
                    reviewId, userId, review.getUseful());
            return review;
        } catch (DataAccessException | ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        try {
            Review review = service.addDislike(reviewId, userId);
            log.info("Дизлайк отзыва с ID #{} пользователя c ID #{} добавлен. Количество лайков отзыва {}",
                    reviewId, userId, review.getUseful());
            return review;
        } catch (DataAccessException | ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        try {
            Review review = service.deleteLike(reviewId, userId);
            log.info("Лайк отзыва с ID #{} пользователя c ID #{} удален. Количество лайков отзыва {}",
                    reviewId, userId, review.getUseful());
            return review;
        } catch (DataAccessException | ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        try {
            Review review = service.deleteDislike(reviewId, userId);
            log.info("Дизлайк отзыва с ID #{} пользователя c ID #{} удален. Количество лайков отзыва {}",
                    reviewId, userId, review.getUseful());
            return review;
        } catch (DataAccessException | ValidationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}