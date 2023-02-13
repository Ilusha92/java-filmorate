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

CREATE TABLE IF NOT EXISTS directors (
     directorId int NOT NULL AUTO_INCREMENT PRIMARY KEY,
     directorName varchar
     );

     CREATE TABLE IF NOT EXISTS directorFilm (
     directorFilmId int NOT NULL AUTO_INCREMENT PRIMARY KEY,
     filmId int,
     directorId int,
     CONSTRAINT filmDirectorFilm FOREIGN KEY (filmId) REFERENCES films (filmId),
     CONSTRAINT directorFilmDirector FOREIGN KEY (directorId) REFERENCES directors (directorId)
     );
