package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        log.info(" '{}' добавлен '{}' в список друзей", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        log.info("'{}' удален '{}' из списка друзей", userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();
        log.info("'{}' запрошенный список общих друзей с пользователем '{}'", userId, friendId);
        if (userFriends.stream().anyMatch(friendFriends::contains)) {
            return userFriends.stream()
                    .filter(userFriends::contains)
                    .filter(friendFriends::contains)
                    .map(userStorage::getUserById).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        Set<Long> friends = user.getFriends();
        if (friends.isEmpty()) {
            log.info("У пользователя 0 друзей");
        }
        return friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}