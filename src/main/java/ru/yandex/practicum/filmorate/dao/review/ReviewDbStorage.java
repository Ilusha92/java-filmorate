package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("reviewDbStorage")
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private static long REVIEW_ID;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikesStorage reviewLikesStorage;
    private final EventDbStorage eventDbStorage;

    @Override
    public Review add(Review review) {
        negativeUserOrFilmCheck(review);
        createId(review);
        String sql = "INSERT INTO review (review_id, content, isPositive, user_id, film_id, useful) " +
                "VALUES (:id, :content, :isPositive, :userId, :filmId, :useful)";
        SqlParameterSource parameterSource = getParameterSource(review);
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.add(review);
        eventDbStorage.saveEvent(review.getUserId(), EventTypes.REVIEW,
                                    OperationTypes.ADD, review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        checkReviewById(review.getReviewId());
        Integer userId = jdbcTemplate.queryForObject("SELECT USER_ID FROM REVIEW WHERE REVIEW_ID = "
                + review.getReviewId(), Integer.class);
        String sql = "UPDATE review SET content = :content, isPositive = :isPositive WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", review.getReviewId())
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        eventDbStorage.saveEvent(userId, EventTypes.REVIEW,
                OperationTypes.UPDATE, review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public Review updateUseful(Review review) {
        String sql = "UPDATE review SET useful = :useful WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("useful", review.getUseful())
                .addValue("id", review.getReviewId());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.update(review);
        return getById(review.getReviewId());
    }

    @Override
    public void delete(long reviewId) {
        checkReviewById(reviewId);
        Review review = getById(reviewId);
        String sql = "DELETE FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", reviewId);
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.delete(reviewId);
        eventDbStorage.saveEvent(Math.toIntExact(review.getUserId()), EventTypes.REVIEW,
                OperationTypes.REMOVE, Math.toIntExact(review.getReviewId()));
    }

    @Override
    public Review getById(long reviewId) {
        if (reviewId <= 0) throw new NotFoundObjectException("Такого отзыва не существует.");
        String sql = "SELECT * FROM review WHERE review_id = ?";
        Review review = jdbcTemplate.queryForObject(sql, this::getRowMapper, reviewId);

        review.getLikes().putAll(reviewLikesStorage.getReviewLikesById(review.getReviewId()));
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, int limit) {
        if (filmId <= 0) throw new NotFoundObjectException("Такого фильма не существует.");
        String sql = "SELECT * FROM review WHERE film_id = :filmId ORDER BY useful DESC LIMIT :limit";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("limit", limit);
        return readReviews(sql, parameterSource);
    }
    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM review ORDER BY useful DESC";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(Review.class);
        return readReviews(sql, parameterSource);
    }

    private List<Review> readReviews(String sql, SqlParameterSource parameterSource) {
        List<Review> reviews = namedParameterJdbcTemplate.queryForStream(sql, parameterSource, this::getRowMapper)
                .collect(Collectors.toList());
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

    private Review getRowMapper(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("isPositive"))
                .useful(rs.getInt("useful"))
                .build();
    }

    private MapSqlParameterSource getParameterSource(Review review) {
        return new MapSqlParameterSource()
                .addValue("id", review.getReviewId())
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("userId", review.getUserId())
                .addValue("filmId", review.getFilmId())
                .addValue("useful", review.getUseful());
    }

    private void checkReviewById(long reviewId) {
        String sql = "SELECT review_id FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", reviewId);
        namedParameterJdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> rs.getLong("review_id"));
    }

    private void createId(Review review) {
        if (review.getReviewId() < REVIEW_ID || review.getReviewId() == 0) review.setReviewId(++REVIEW_ID);
        REVIEW_ID = review.getReviewId();
    }

    private void negativeUserOrFilmCheck(Review review) throws NotFoundObjectException {
        if (review.getFilmId() < 0 || review.getUserId() < 0)
            throw new NotFoundObjectException("Такого пользовователя или фильма не существует.");
    }
}