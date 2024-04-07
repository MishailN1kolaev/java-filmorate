package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    int deleteUserById(int id);

    User getUserById(int id);

    List<User> getAllUser();

    User addFriendById(int userId, int friendId);

    User deleteFriendById(int userId, int friendId);

    List<User> getAllFriends(int id);

    List<User> getСommonFriends(int firstUserId, int secondUserId);

    int deleteAllUsers();

}
