package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundObjectException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class User {

    private int id;
    @Email(message = "Email is incorrect")
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Login is required")
    @Pattern(regexp = "\\S+", message = "Login must not contain space characters")
    private String login;
    private String name;
    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday must not be later than the current date")
    private LocalDate birthday;
    private Set<Integer> friendIds = new TreeSet<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriendId(int userId){
        friendIds.add(userId);
    }

    public Set<Integer> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(Set<Integer> friendIds) {
        this.friendIds = friendIds;
    }

    public void removeFriend(Integer userId){
        if(friendIds.contains(userId)){
            friendIds.remove(userId);
        }else{
            throw new NotFoundObjectException("Пользователя "+userId+" нет в друзьях у пользователя "+this.id+"!");
        }
    }

}
