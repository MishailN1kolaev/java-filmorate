package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataBaseExeption;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_GENRE_ID = "SELECT id, name FROM genres WHERE id = ?";
    private static final String SELECT_ALL_GENRES = "SELECT id, name FROM genres ";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_GENRE_ID, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            log.error("Жанр не найден в БД, id: {}, ошибка: {}",  id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> genres = jdbcTemplate.query(SELECT_ALL_GENRES, this::mapRowToGenre);
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws DataBaseExeption {
        try {
            Genre genre = new Genre();
            genre.setId(resultSet.getInt("id"));
            genre.setName(resultSet.getString("name"));
            return genre;
        } catch (SQLException e) {
            log.error("Неудача в обработке ответа из БД, rs: {}, ошибка: {}",  resultSet, e.getMessage());
            throw new DataBaseExeption("Ошибка при обработке ответа от БД" + e.getClass() + e.getMessage());
        }
    }
}
