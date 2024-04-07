package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataBaseExeption;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_MPA_ID = "SELECT id, name FROM mpa WHERE id = ?";
    private static final String SELECT_ALL_MPA = "SELECT id, name FROM mpa ";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_MPA_ID, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            log.error("Райтинг не найден в БД, id: {}, ошибка: {}",  id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        List<Mpa> allMpa = jdbcTemplate.query(SELECT_ALL_MPA, this::mapRowToMpa);
        allMpa.sort(Comparator.comparing(Mpa::getId));
        return allMpa;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws DataBaseExeption {
        try {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("id"));
            mpa.setName(resultSet.getString("name"));
            return mpa;
        } catch (SQLException e) {
            log.error("Неудача в обработке ответа из БД, rs: {}, ошибка: {}",  resultSet, e.getMessage());
            throw new DataBaseExeption("Ошибка при обработке ответа от БД" + e.getClass() + e.getMessage());
        }
    }
}
