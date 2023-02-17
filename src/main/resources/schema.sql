CREATE TABLE IF NOT EXISTS films (
    filmId INTEGER PRIMARY KEY,
    name varchar(200),
    description varchar,
    release_date date,
    duration int,
    mpaId int
);

CREATE TABLE IF NOT EXISTS users (
    userId INTEGER PRIMARY KEY,
    email varchar,
    login varchar,
    name varchar,
    birthdate date
);

CREATE TABLE IF NOT EXISTS film_genre (
    filmId int,
    genreId int
);

CREATE TABLE IF NOT EXISTS genre (
    genreId int,
    name varchar
);

CREATE TABLE IF NOT EXISTS likesList (
    filmId int,
    userId int
);

CREATE TABLE IF NOT EXISTS friendship (
    userId int,
    friendId int,
    friendshipStatusId int
);

CREATE TABLE IF NOT EXISTS mpa (
    mpaId int,
    name varchar
);

CREATE TABLE IF NOT EXISTS friendshipStatus (
    friendshipStatusId int,
    description varchar
);

CREATE TABLE IF NOT EXISTS review (
    review_id LONG PRIMARY KEY,
    content varchar NOT NULL CHECK (content <> ' '),
    isPositive boolean NOT NULL,
    user_id LONG REFERENCES users (userId),
    film_id LONG REFERENCES films (filmId),
    useful int
);

CREATE TABLE IF NOT EXISTS review_likes (
    id LONG GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id LONG REFERENCES users (userId) ON DELETE CASCADE,
    review_id LONG REFERENCES review (review_id) ON DELETE CASCADE,
    isLike BOOLEAN NOT NULL
);