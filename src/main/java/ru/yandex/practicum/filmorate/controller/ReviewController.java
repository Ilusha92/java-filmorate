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

    // Одинаковый путь для эндпоинтов getAllReviews & getReviews

    @GetMapping(params = {""})
    public List<Review> getAllReviews() {
        try {
            List<Review> reviews = storage.getAllReviews();
            log.info("Список всех ревью в размере {} переданы.", reviews.size());
            return reviews;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        try {
            Review review = storage.getById(reviewId);
            log.info("Обзор с ID #{} пользователя с ID #{} передан", review.getId(), review.getUserId());
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
            log.info("Отзыв с ID #{} добавлен.", review.getId());
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
            log.info("Отзыв с ID #{} обновлен.", review.getId());
            return review;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping
    public Review deleteReview(@RequestBody @Valid Review review) {
        try {
            storage.delete(review);
            log.info("Отзыв с ID #{} удален.", review.getId());
            return review;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping(params = {"count"})
    public List<Review> getReviews(@RequestParam(defaultValue = "10") Integer count) {
        try {
            List<Review> reviews = storage.getReviewsWithLimit(count);
            log.info("Ревью в размере {} переданы", count);
            return reviews;
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping(params = {"filmId", "count"})
    public List<Review> getReviewsWithFilmId(
            @RequestParam Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        try {
            List<Review> reviews = storage.getAllReviewsByFilmId(filmId, count);
            log.info("Ревью в размере {} фильма с ID {}", count, filmId);
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