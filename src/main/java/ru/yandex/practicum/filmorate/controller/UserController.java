package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.Exception;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @ResponseBody
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        userValidation(user);
        users.put(user.getId(), user);
        log.info("Пользователь '{}' был сохранен с идентификатором '{}'", user.getEmail(), user.getId());
        return user;
    }

    @ResponseBody
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        userValidation(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("'{}'информация с идентификатором '{}' была обновлена", user.getLogin(), user.getId());
        } else {
            throw new Exception("Попытка обновить несуществующего пользователя");
        }
        return user;
    }

    @ResponseBody
    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей: '{}'", users.size());
        return new ArrayList<>(users.values());
    }

    private void userValidation(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new Exception("Неверный адрес электронной почты пользователя с идентификатором '" + user.getId() + "'");
        }
        if (user.getLogin().isBlank() || user.getLogin().isEmpty()) {
            throw new Exception("Введен неверный логин  с использованием идентификатора пользователя'" + user.getId() + "'");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя с идентификатором '{}' было задано '{}'", user.getId(), user.getName());
        }
        if (user.getBirthday().isAfter(LocalDate.now()) || user.getBirthday() == null) {
            throw new Exception("Неверная дата рождения пользователя с идентификатором'" + user.getId() + "'");
        }
        if (user.getId() == 0 || user.getId() < 0) {
            user.setId(++id);
            log.info("Задан неверный идентификатор пользователя '{}'", user.getId());
        }
    }
}