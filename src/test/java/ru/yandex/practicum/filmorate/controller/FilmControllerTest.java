//package ru.yandex.practicum.filmorate.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.exceptions.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDate;
//
//public class FilmControllerTest {
//
//    FilmController filmController = new FilmController();
//
//    @BeforeEach
//    public void value(){
//        Film film = new Film("Фильм","Описание", LocalDate.of(1990,1,1), 100);
//        filmController.createFilm(film);
//    }
//
//    @Test
//    public void equalityFilm(){
//        for (Film film : filmController.getAllFilms()){
//            assertEquals(film.getId(),1);
//            assertEquals(film.getName(),"Фильм");
//            assertEquals(film.getDescription(),"Описание");
//            assertEquals(film.getReleaseDate(), LocalDate.of(1990,1,1));
//            assertEquals(film.getDuration(), 100);
//        }
//    }
//
//    @Test
//    public void createFilm() {
//        assertEquals(filmController.getAllFilms().size(), 1);
//    }
//
//    @Test
//    public void updateFilm(){
//        Film film = new Film("Фильм","Описание", LocalDate.of(1990,1,1), 100);
//        film.setId(1);
//        filmController.updateFilm(film);
//
//        for (Film test :filmController.getAllFilms()){
//            assertEquals(test,film);
//        }
//    }
//
//    @Test
//    @DisplayName("Не валидные значения")
//    public void notValidateFilm(){
//        Film noName = new Film("1","Описание1", LocalDate.of(1990,1,1),100);
//        Film noDate = new Film("2","Описание2", LocalDate.of(1800,1,1),100);
//        Film descriptionMax200 = new Film("Фильм","Хакер‑подросток Дейв Лайтмен взламывает \n"+
//                " компьютерную сеть и находит среди файлов несколько любопытных военных симуляторов. \n" +
//                "Фильм стал вторым в карьере молодого Мэттью Бродерика и открыл ему дорогу в большое кино. \n"
//                , LocalDate.of(2000,7,21),1);
//
//        try {
//            filmController.createFilm(noName);
//        }catch (ValidationException v){
//            assertNotEquals("",v.getMessage());
//        }
//
//        try {
//            filmController.createFilm(noDate);
//        }catch (ValidationException v){
//            assertNotEquals("",v.getMessage());
//        }
//
//        try {
//            filmController.createFilm(descriptionMax200);
//        }catch (ValidationException v){
//            assertNotEquals("",v.getMessage());
//        }
//    }
//}