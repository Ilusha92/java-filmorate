package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService service;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Long reviewId) {
        Review review = service.getById(reviewId);
        log.info("Обзор с ID #{} пользователя с ID #{} передан", review.getReviewId(), review.getUserId());
        return review;

    }

    @PostMapping
    public Review postReview(@RequestBody @Valid Review review) {
        review = service.add(review);
        log.info("Отзыв с ID #{} добавлен.", review.getReviewId());
        return review;
    }

    @PutMapping
    public Review putReview(@RequestBody @Valid Review review) {
        review = service.update(review);
        log.info("Отзыв с ID #{} обновлен.", review.getReviewId());
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") long reviewId) {
        service.delete(reviewId);
        log.info("Отзыв с ID #{} удален.", reviewId);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        List<Review> reviews;
        if (filmId == null) {
            reviews = service.getAllReviews();
            log.info("Передан список всех отзывов в размере {}", reviews.size());
            return reviews;
        }
        reviews = service.getAllReviewsByFilmId(filmId, count);
        log.info("Список отзывов в размере {} фильма с ID {}", count, filmId);
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        Review review = service.addLike(reviewId, userId);
        log.info("Лайк отзыв с ID #{} пользователя c ID #{} добавлен. Количество лайков отзыва {}",
                reviewId, userId, review.getUseful());
        return review;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        Review review = service.addDislike(reviewId, userId);
        log.info("Дизлайк отзыва с ID #{} пользователя c ID #{} добавлен. Количество лайков отзыва {}",
                reviewId, userId, review.getUseful());
        return review;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        Review review = service.deleteLike(reviewId, userId);
        log.info("Лайк отзыва с ID #{} пользователя c ID #{} удален. Количество лайков отзыва {}",
                reviewId, userId, review.getUseful());
        return review;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable Long userId) {
        Review review = service.deleteDislike(reviewId, userId);
        log.info("Дизлайк отзыва с ID #{} пользователя c ID #{} удален. Количество лайков отзыва {}",
                reviewId, userId, review.getUseful());
        return review;
    }
}