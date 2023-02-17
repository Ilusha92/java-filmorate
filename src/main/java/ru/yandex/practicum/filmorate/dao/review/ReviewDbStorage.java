package ru.yandex.practicum.filmorate.dao.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository("reviewDbStorage")
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private static long REVIEW_ID;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ReviewLikesStorage reviewLikesStorage;

    @Override
    public Review add(Review review) {
        createId(review);
        String sql = "INSERT INTO review (review_id, content, isPositive, user_id, film_id, useful) " +
                "VALUES (:id, :content, :isPositive, :userId, :filmId, :useful)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(review);
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.add(review);
        return review;
    }

    @Override
    public Review update(Review review) {
        checkReviewById(review);
        String sql = "MERGE INTO review KEY(review_id) VALUES (:id, :content, :isPositive, :userId, :filmId, :useful)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(review);
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.update(review);
        return review;
    }

    @Override
    public Review delete(Review review) {
        checkReviewById(review);
        String sql = "DELETE FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(review);
        namedParameterJdbcTemplate.update(sql, parameterSource);
        reviewLikesStorage.delete(review);
        return review;
    }

    @Override
    public Review getById(long reviewId) {
        String sql = "SELECT * FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("review_id", reviewId);
        Review review = namedParameterJdbcTemplate.queryForObject(sql, parameterSource, this::getRowMapper);

        review.getLikes().putAll(reviewLikesStorage.getReviewLikesById(review.getId()));
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, int limit) {
        String sql = "SELECT * FROM review WHERE film_id = :filmId ORDER BY useful DESC LIMIT :limit";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("limit", limit);
        return readReviews(sql, parameterSource);
    }

    @Override
    public List<Review> getReviewsWithLimit(int limit) {
        String sql = "SELECT * FROM review ORDER BY useful DESC LIMIT :limit";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("limit", limit);
        return readReviews(sql, parameterSource);
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM review ORDER BY useful DESC";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(Review.class);
        return readReviews(sql, parameterSource);
    }

    private List<Review> readReviews(String sql, SqlParameterSource parameterSource) {
        Stream<Review> reviews = namedParameterJdbcTemplate.queryForStream(sql, parameterSource, this::getRowMapper);
        Map<Long, Boolean> reviewsLikes = reviewLikesStorage.getAllReviewLikes();
        if (reviewsLikes.size() > 0) {
            reviews.forEach(review -> reviewsLikes.forEach((userId, like) -> {
                if (review.getUserId() == userId) review.getLikes().put(userId, like);
            }));
        }
        return reviews.collect(Collectors.toList());
    }

    private Review getRowMapper(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .id(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .isPositive(rs.getBoolean("isPositive"))
                .useful(rs.getInt("useful"))
                .build();
    }

    private void checkReviewById(Review review) {
        String sql = "SELECT review_id FROM review WHERE review_id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", review.getId());
        namedParameterJdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) -> rs.getLong("review_id"));
    }

    private void createId(Review review) {
        if (review.getId() < REVIEW_ID || review.getId() == 0) review.setId(++REVIEW_ID);
        REVIEW_ID = review.getId();
    }
}