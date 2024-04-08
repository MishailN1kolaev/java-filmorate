package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Slf4j
@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_NEW_USER = "insert into users(login, name, email, birthday) " +
            "values (?, ?, ?, ?)";
    private static final String UPDATE_USER = "update users set " +
            "login = ?, name = ?, email = ?, birthday = ? " +
            "where id = ?";
    private static final String DELETE_ID_USER = "delete from users where id = ?";
    private static final String SELECT_ID_USER = "select * from users where id = ?";
    private static final String SELECT_ALL_USER = "select * from users ";
    private static final String DELETE_ALL_USER = "delete from users";
    private static final String INSERT_FRIEND = "insert into friends(user_id, friend_id) values (?, ?)";
    private static final String DELETE_FRIEND = "delete from friends where user_id = ? and friend_id = ? ";
    private static final String SELECT_ALL_ID_FRIENDS = "select friend_id from friends where user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_USER, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getBirthday().toString());
            return stmt;
        }, keyHolder);
        try {
            int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            user.setId(userId);
        } catch (NullPointerException e) {
            log.error("Ошибка при добавлении пользователя в БД: {}, ошибка: {}",  user, e.getMessage());
            throw new IncorrectIdException("id пользователя не вернулся при добавлении в БД");
        }
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        int countRecord = jdbcTemplate.update(UPDATE_USER,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        if (countRecord > 0) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int deleteUserById(int id) {
        return jdbcTemplate.update(DELETE_ID_USER, id);
    }

    @Override
    public Optional<User> getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_ID_USER, id);
        if (userRows.next()) {
            User user = new User();
            user.setId(userRows.getInt("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            try {
                user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
            } catch (NullPointerException e) {
                log.error("Ошибка при извлечении данных пользователя из БД: {}, ошибка: {}",  id, e.getMessage());
                throw new IncorrectIdException("Дата рождения пользователя не получена из БД");
            }
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUser() {
        return jdbcTemplate.query(SELECT_ALL_USER, this::mapRowToUser);
    }

    @Override
    public int deleteAllUsers() {
        return jdbcTemplate.update(DELETE_ALL_USER);
    }

    @Override
    public int addFriendById(int userId, int friendId) {
        return jdbcTemplate.update(INSERT_FRIEND, userId, friendId);
    }

    @Override
    public int deleteFriendById(int userId, int friendId) {
        return jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public List<Integer> getAllIdFriends(int id) {
        List<Integer> friends = jdbcTemplate.query(SELECT_ALL_ID_FRIENDS, this::mapRowToInteger, id);
        return friends;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private int mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("friend_id");
    }

}
