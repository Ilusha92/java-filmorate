package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;


public class UserControllerTest {
    UserController userControllerTest;

    @BeforeEach
    public void value(){
        User user = new User("Y@yandex.ru","Vl","", LocalDate.of(1990,2,20));
        userControllerTest.createUser(user);
    }

    @Test
    @DisplayName("Проверка сохраненного User")
    public void equalityUserTest(){

        for(User user : userControllerTest.getAllUsers()){
            assertEquals(user.getId(), 1);
            assertEquals(user.getLogin(), "Vl");
            assertEquals(user.getName(), "Vl");
            assertEquals(user.getEmail(), "Y@yandex.ru");
            assertEquals(user.getBirthday(), LocalDate.of(1990,2,20));
        }
    }

    @Test
    @DisplayName("Сохранение User")
    public void createUserTest(){
        assertEquals(userControllerTest.getAllUsers().size(),1);
    }

    @Test
    @DisplayName("Обновление User")
    public void updateUser(){
        User user =  new User("d@ya.ru","AD","",LocalDate.of(1980,10,15));
        user.setId(1);
        userControllerTest.updateUser(user);

        for (User test :userControllerTest.getAllUsers()){
            assertEquals(test,user);
        }
    }

    @Test
    @DisplayName("Не валидные значения")
    public void notValidateUser(){
        User notValidEmail = new User("yandex.ru","Vlad","Влад", LocalDate.of(1990,2,20));
        User notValidDate = new User("Y@yandex.ru","Vlad","Влад", LocalDate.of(2050,2,20));
        User notValidLogin = new User("Y@yandex.ru","V l a d","Влад", LocalDate.of(2050,2,20));

        try {
            userControllerTest.createUser(notValidEmail);
        }catch (ValidationException v){
            assertNotEquals("",v.getMessage());
        }

        try {
            userControllerTest.createUser(notValidDate);
        }catch (ValidationException v){
            assertNotEquals("",v.getMessage());
        }

        try {
            userControllerTest.createUser(notValidLogin);
        }catch (ValidationException v){
            assertNotEquals("",v.getMessage());
        }
    }
}