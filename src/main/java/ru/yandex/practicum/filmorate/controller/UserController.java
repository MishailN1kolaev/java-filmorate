package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Получен запрос к эндпоинту: GET /users '");
        return userService.getAllUser();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{id}");
        User user = userService.getUserById(id);
        log.info("Ответ: GET /users/{id} ', '{}' ",user);
        return user;
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users ', Строка параметров запроса: '{}'", user);
        User newUser = userService.addUser(user);
        log.info("Ответ на запрос к эндпоинту: POST /users ', '{}'", user);
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", user);
        User updUser = userService.updateUser(user);
        log.info("Ответ на запрос к эндпоинту: PUT /users ', Строка параметров запроса: '{}'", updUser);
        return updUser;
    }

   @PutMapping("/users/{id}/friends/{friendId}")
   public User addUserFriend(@PathVariable int id, @PathVariable int friendId) {
       log.info("Получен запрос к эндпоинту: PUT /users/{}/friends/{} '", id, friendId);
       return userService.addFriendById(id,friendId);
   }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteUserFriendById(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{}/friends/{} '", id, friendId);
        return userService.deleteFriendById(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{}/friends '", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getСommonFriends(id, otherId);
    }

    @DeleteMapping("/users")
    public void deleteAllUsers() {
        log.info("Получен запрос к эндпоинту: DELETE /users ");
        userService.deleteAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{id} ");
        userService.deleteUserById(id);
    }

}