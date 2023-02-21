package ru.yandex.practicum.filmorate.dao.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikesStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("reviewLikesDbStorage")
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Review review) {
        Map<Long, Boolean> likes = review.getLikes();

        if (likes.size() > 0) {
            String sql = getInsertSql(likes, review);
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public void update(Review review) {
        Map<Long, Boolean> reviewLikes = review.getLikes();

        if (reviewLikes.size() > 0) {
            String sql = getInsertSql(reviewLikes, review);
            delete(review.getReviewId());
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public void delete(long reviewId) {
        String sql = "DELETE FROM review_likes WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", reviewId);
        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public Map<Long, Boolean> getReviewLikesById(long reviewId) {
        String sql = "SELECT user_id, isLike FROM review_likes WHERE review_id = ?";
        List<ReviewLikesRow> likesRow = jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                new ReviewLikesRow(rs.getLong("user_id"), rs.getBoolean("isLike")), reviewId)
                .collect(Collectors.toList());
        Map<Long, Boolean> reviewLikes = new HashMap<>();
        if (likesRow.size() > 0) {
            likesRow.forEach(reviewLikesRow ->
                    reviewLikes.put(reviewLikesRow.getUserId(), reviewLikesRow.isLike()));
        }
        return reviewLikes;
    }

    @Override
    public Map<Long, Boolean> getAllReviewLikes() {
        String sql = "SELECT user_id, isLike FROM review_likes";
        List<ReviewLikesRow> likesRow = jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                new ReviewLikesRow(rs.getLong("user_id"), rs.getBoolean("isLike")))
                .collect(Collectors.toList());
        Map<Long, Boolean> reviewLikes = new HashMap<>();
        if (likesRow.size() > 0) {
            likesRow.forEach(reviewLikesRow ->
                    reviewLikes.put(reviewLikesRow.getUserId(), reviewLikesRow.isLike()));
        }
        return reviewLikes;
    }

    private String getInsertSql(Map<Long, Boolean> reviewLikes, Review review) {
        int commaAndSpace = 2;
        long reviewId = review.getReviewId();
        StringBuilder sql = new StringBuilder("INSERT INTO review_likes (user_id, review_id, isLike) VALUES ");
        reviewLikes.forEach((userId, isLike) -> sql.append(String.format("(%d, %d, %b), ", userId, reviewId, isLike)));
        sql.setLength(sql.length() - commaAndSpace);
        return sql.toString();
    }

    @Data
    @AllArgsConstructor
    private class ReviewLikesRow {
        private long userId;
        private boolean isLike;
    }
}