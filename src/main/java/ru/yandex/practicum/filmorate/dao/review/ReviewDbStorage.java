package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository("reviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        negativeUserOrFilmCheck(review);
        String sql = "INSERT INTO review (content, isPositive, user_id, film_id, useful) " +
                "VALUES (:content, :isPositive, :userId, :filmId, :useful)";
        SqlParameterSource parameterSource = getParameterSource(review);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder);
        review.setReviewId((long) keyHolder.getKey());
        return review;
    }

    @Override
    public Review update(Review review) {
        checkReviewById(review.getReviewId());
        String sql = "UPDATE review SET content = :content, isPositive = :isPositive WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", review.getReviewId())
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        return getById(review.getReviewId());
    }

    @Override
    public Review updateUseful(Review review) {
        String sql = "UPDATE review SET useful = :useful WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("useful", review.getUseful())
                .addValue("id", review.getReviewId());
        namedParameterJdbcTemplate.update(sql, parameterSource);
        return getById(review.getReviewId());
    }

    @Override
    public void delete(long reviewId) {
        checkReviewById(reviewId);
        String sql = "DELETE FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", reviewId);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public Review getById(long reviewId) {
        if (reviewId <= 0) throw new NotFoundObjectException("Такого отзыва не существует.");
        String sql = "SELECT * FROM review WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::getRowMapper, reviewId);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, int limit) {
        if (filmId <= 0) throw new NotFoundObjectException("Такого фильма не существует.");
        String sql = "SELECT * FROM review WHERE film_id = :filmId ORDER BY useful DESC LIMIT :limit";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("limit", limit);
        return namedParameterJdbcTemplate.queryForStream(sql, parameterSource, this::getRowMapper)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM review ORDER BY useful DESC";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(Review.class);
        return namedParameterJdbcTemplate.queryForStream(sql, parameterSource, this::getRowMapper)
                .collect(Collectors.toList());
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

    private void negativeUserOrFilmCheck(Review review) throws NotFoundObjectException {
        if (review.getFilmId() < 0 || review.getUserId() < 0)
            throw new NotFoundObjectException("Такого пользовователя или фильма не существует.");
    }
}