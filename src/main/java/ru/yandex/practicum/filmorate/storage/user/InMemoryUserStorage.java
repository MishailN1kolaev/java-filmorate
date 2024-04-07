package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User>  users = new HashMap<>();
    private Long id;

    public InMemoryUserStorage() {
        id = 0L;
    }

    @Override
    public User createUser(User user) {
        validate(user);
        users.put(user.getId(), user);
        log.info("Пользователь '{}' был сохранен с идентификатором '{}'", user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            validate(user);
            users.put(user.getId(), user);
            log.info("{}' информация с идентификатором '{}' была обновлена", user.getLogin(), user.getId());
            return user;
        } else {
            throw new EntityNotFoundException("Попытка обновить несуществующего пользователя");
        }
    }

    @Override
    public void deleteUsers() {
        users.clear();
        log.info("Список пользователь очищен");
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователя с идентификатором '" + id + "' не существует");
        }
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        log.info("Количество пользователей: '{}'", users.size());
        return new ArrayList<>(users.values());
    }

    private void validate(User user) {
        if (user.getBirthday().isAfter(LocalDate.now()) || user.getBirthday() == null) {
            throw new ValidationException("Неверный день рождения пользователя с идентификатором '" + user.getId() + "'");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Неверный адрес электронной почты пользователя с идентификатором '" + user.getId() + "'");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя с идентификатором '{}' было задано как '{}'", user.getId(), user.getName());
        }
        if (user.getLogin().isBlank() || user.getLogin().isEmpty()) {
            throw new ValidationException("Неверный логин пользователя с идентификатором '" + user.getId() + "'");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (user.getId() == null || user.getId() <= 0) {
            user.setId(++id);
            log.info("'{}' присвоен идентификатор '{}'", user.getEmail(), user.getId());
        }
    }
}