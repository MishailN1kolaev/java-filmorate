package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.validation.Validation;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final Validation validator;

    @Autowired
    public UserServiceImpl(@Qualifier("UserDbStorage") UserStorage storage, Validation validator) {
        this.storage = storage;
        this.validator = validator;
    }

    @Override
    public User addUser(User user) {
        validateUser(user, "POST");
        setNameOrLoginUser(user);
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        validateUser(user, "PUT");
        setNameOrLoginUser(user);
        Optional<User> updUser = storage.updateUser(user);
        if (updUser.isEmpty()) {
            throw new IncorrectIdException("нет пользователя с таким id");
        }
        return updUser.get();
    }

    @Override
    public int deleteUserById(int id) {
        return storage.deleteUserById(id);
    }

    @Override
    public User getUserById(int id) {
        User user = storage.getUserById(id).orElseThrow(() -> new IncorrectIdException("нет пользователя с таким id"));
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return storage.getAllUser();
    }

    @Override
    public User addFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(friendId);
        if (user.isEmpty() || friend.isEmpty()) {
            throw new IncorrectIdException("Пользователь не найден");
        }
        int i = storage.addFriendById(userId, friendId);
        if (i <= 0) {
            throw new IncorrectIdException("Пользователь уже добавлен в друзья");
        }
        Set<Integer> userFriends = new HashSet<>(storage.getAllIdFriends(userId));
        user.get().getFriends().clear();
        user.get().getFriends().addAll(userFriends);
        return user.get();
    }

    @Override
    public User deleteFriendById(int userId, int friendId) {
        Optional<User> user = storage.getUserById(userId);
        Optional<User> friend = storage.getUserById(friendId);
        if (user.isEmpty() || friend.isEmpty()) {
            throw new IncorrectIdException("Пользователь не найден");
        }
        int i = storage.deleteFriendById(userId, friendId);
        if (i <= 0) {
            throw new IncorrectIdException("Пользователь не найден в друзьях");
        }
        Set<Integer> userFriends = new HashSet<>(storage.getAllIdFriends(userId));
        user.get().getFriends().clear();
        user.get().getFriends().addAll(userFriends);
        return user.get();
    }

    @Override
    public List<User> getAllFriends(int id) {
        Optional<User> user = storage.getUserById(id);
        if (user.isEmpty()) {
            throw new IncorrectIdException("Пользователь не найден");
        }
        ArrayList<User> userFriends = new ArrayList<>();
        Set<Integer> idFriends = new HashSet<>(storage.getAllIdFriends(id));
        if (idFriends.isEmpty()) {
            return userFriends;
        }
        for (Integer i : idFriends) {
            Optional<User> currentUser = storage.getUserById(i);
            if (currentUser.isPresent()) {
                userFriends.add(currentUser.get());
            }
        }
        return userFriends;
    }

    @Override
    public List<User> getСommonFriends(int firstUserId, int secondUserId) {
        Optional<User> firstUser = storage.getUserById(firstUserId);
        Optional<User> secondUser = storage.getUserById(secondUserId);
        if (firstUser.isEmpty() || secondUser.isEmpty()) {
            throw new IncorrectIdException("Пользователь не найден");
        }
        ArrayList<User> commonFriends = new ArrayList<>();
        Set<Integer> firstFreinds = new HashSet<>(storage.getAllIdFriends(firstUser.get().getId()));
        Set<Integer> secondFreinds = new HashSet<>(storage.getAllIdFriends(secondUser.get().getId()));
        if (firstFreinds.isEmpty()) {
            return commonFriends;
        } else if (secondFreinds.isEmpty()) {
            return commonFriends;
        } else {
            return firstFreinds.stream()
                    .filter(secondFreinds::contains)
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public int deleteAllUsers() {
        int idResult = storage.deleteAllUsers();
        if (idResult == 0) {
            throw new ValidationException("не получилось очистить таблицу пользователей");
        }
        log.error("Пользователи удалены:  ', код ответа: '{}'", idResult);
        return idResult;
    }

    private void validateUser(User user, String method) {
        String email = user.getEmail();
        if ((email == null) || email.isBlank() || (!validator.isHasEmailSymbol(email))
                || validator.isHasSpaceSymbol(email)) {
            throw new ValidationException("некоректные данные в почте");
        }
        String login = user.getLogin();
        if (validator.isHasSpaceSymbol(login)) {
            throw new ValidationException("логин должен одно слово, не может быть пустым");
        }
        LocalDate date = user.getBirthday();
        if ((date != null) && (!validator.isDateUserOk(date))) {
            throw new ValidationException("дата рождения не может быть больше текущей даты");
        }
    }

    private void setNameOrLoginUser(User user) {
        String name = user.getName();
        if ((name == null) || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
