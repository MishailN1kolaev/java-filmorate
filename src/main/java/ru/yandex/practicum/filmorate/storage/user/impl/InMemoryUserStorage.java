package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id;
    private final Map<Integer,User> users;

    public InMemoryUserStorage() {
        this.id = 0;
        this.users = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        id++;
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            User currentUser = users.get(userId);
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            currentUser.setBirthday(user.getBirthday());
            currentUser.setLogin(user.getLogin());
            return Optional.of(users.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public int deleteUserById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return id;
        }
        return 0;
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int deleteAllUsers() {
        users.clear();
        if (users.size() > 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int addFriendById(int userId, int friendId) {
        return 0;
    }

    @Override
    public int deleteFriendById(int userId, int friendId) {
        return 0;
    }

    @Override
    public List<Integer> getAllIdFriends(int id) {
        return null;
    }

}
