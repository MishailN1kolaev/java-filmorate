package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DataBaseExeption;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.Objects;
import java.util.Comparator;
import java.util.Collections;

@Slf4j
@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private static final String INSERT_NEW_FIlM = "insert into films(name, description, duration, " +
            "release_date, rating_id) " +
            "values (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE = "insert into films_genres(film_id, genre_id) " +
            "values (?, ?)";
    private static final String UPDATE_FILM = "update films set " +
            "name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
            "where id = ?";
    private static final String DELETE_FILM_GENRE = "delete from films_genres WHERE film_id = ? ";
    private static final String DELETE_FILM = "delete from films where id = ?";
    private static final String SELECT_FILM_ID = "SELECT f.id AS id, f.name AS name, f.description AS description, " +
            "f.duration AS duration, f.release_date AS release_date, f.rating_id AS rating_id, r.name AS mpa_name, " +
            "fg.GENRE_ID AS genre_id, g.name AS genre_name FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID " +
            "LEFT JOIN FILMS_GENRES fg ON f.ID = fg.FILM_ID LEFT JOIN GENRES g ON fg.GENRE_ID = g.ID " +
            "WHERE f.id = ?";
    private static final String DELETE_ALL_FILM = "delete from films";
    private static final String INSERT_LIKE = "insert into films_like_users(film_id, user_id) values (?, ?)";
    private static final String DELETE_LIKE = "delete from films_like_users where film_id = ? and user_id = ? ";
    private static final String SELECT_GENRES_FILM = "select genre_id FROM films_genres WHERE film_id = ? ";
    private static final String SELECT_LIKES_FILM = "select user_id as id FROM films_like_users WHERE film_id = ? ";
    private static final String SELECT_ALL_FILMS_GENRES = "SELECT f.id AS id, f.name AS name, " +
            "f.description AS description, f.duration AS duration, f.release_date AS release_date, " +
            "f.rating_id AS rating_id, r.name AS mpa_name, fg.GENRE_ID AS genre_id, g.name AS genre_name, " +
            "fl.user_id as like_id " +
            "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID " +
            "LEFT JOIN FILMS_GENRES fg ON f.ID = fg.FILM_ID LEFT JOIN GENRES g ON fg.GENRE_ID = g.ID " +
            "LEFT JOIN films_like_users fl ON f.ID = fl.film_id";
    private static final String SELECT_TOP_FILM_GENRES = "SELECT f.id AS id, f.name AS name, " +
            "f.description AS description, f.duration AS duration, f.release_date AS release_date, " +
            "f.rating_id AS rating_id, r.name AS mpa_name, fg.GENRE_ID AS genre_id, g.name AS genre_name, " +
            "fl.user_id as like_id " +
            "FROM films AS f INNER JOIN mpa AS r ON f.RATING_ID = r.ID " +
            "LEFT JOIN FILMS_GENRES fg ON f.ID = fg.FILM_ID LEFT JOIN GENRES g ON fg.GENRE_ID = g.ID " +
            "LEFT JOIN films_like_users fl ON f.ID = fl.film_id " +
            "WHERE f.id in " +
            "(select f.id AS id  " +
            "FROM films AS f LEFT OUTER JOIN FILMS_LIKE_USERS l ON f.ID = l.FILM_ID " +
            "GROUP BY f.ID " +
            "ORDER BY count(l.USER_ID) DESC, f.ID ASC " +
            "limit ?)";
    private static final String SELECT_TOP_FILM_ID = "select f.id AS id " +
            "FROM films AS f " +
            "LEFT OUTER JOIN FILMS_LIKE_USERS l ON f.ID = l.FILM_ID " +
            "GROUP BY f.ID " +
            "ORDER BY count(l.USER_ID) DESC, f.ID ASC " +
            "limit ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_NEW_FIlM, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getDuration());
            stmt.setString(4, film.getReleaseDate().toString());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        try {
            int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
            film.setId(filmId);
        } catch (NullPointerException e) {
            log.error("Ошибка при добавлении фильма в БД: {}, ошибка: {}",  film, e.getMessage());
            throw new IncorrectIdException("id фильма не вернулся при добавлении в БД");
        }
        ArrayList<Genre> genres = new ArrayList<>(film.getGenres());
        if (genres.isEmpty()) {
            return film;
        }
        try {
            insertGenresFilm(film.getId(), genres);
        } catch (Throwable e) {
            log.error("Ошибка при добавлении жанров для фильма в БД, film: {}, ошибка: {}",  film.getId(),
                    e.getMessage());
            throw new DataBaseExeption("Ошибка при обработке ответа от БД" + e.getClass() + e.getMessage());
        }
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        int filmId = film.getId();
        int countRecord = jdbcTemplate.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                filmId);
        if (countRecord <= 0) {
            return Optional.empty();
        }
        List<Genre> filmGenresNew = new ArrayList<>(film.getGenres());
        List<Genre> filmGenresOld = getAllGenreByFilmId(filmId);
        if (filmGenresOld.isEmpty() && filmGenresNew.isEmpty()) {
            return Optional.of(film);
        } else if (filmGenresNew.isEmpty()) {
            jdbcTemplate.update(DELETE_FILM_GENRE, filmId);
        } else {
            jdbcTemplate.update(DELETE_FILM_GENRE, filmId);
            try {
                insertGenresFilm(filmId, filmGenresNew);
            } catch (Throwable e) {
                log.error("Ошибка при добавлении жанров для фильма в БД, film: {}, ошибка: {}",  filmId,
                        e.getMessage());
                throw new DataBaseExeption("Ошибка при обработке ответа от БД" + e.getClass() + e.getMessage());
            }
            List<Genre> allGenresStorage = genreStorage.getAllGenres();
            for (Genre curntGenre: filmGenresNew) {
                int genreId = curntGenre.getId();
                for (int i = 0; i < allGenresStorage.size(); i++) {
                    if (genreId == allGenresStorage.get(i).getId()) {
                        curntGenre.setName(allGenresStorage.get(i).getName());
                        break;
                    }
                }
            }
            filmGenresNew.sort(Comparator.comparing(Genre::getId));
            film.getGenres().clear();
            film.getGenres().addAll(filmGenresNew);
        }
        return Optional.of(film);
    }

    @Override
    public int deleteFilmById(int id) {
        return jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            Optional<Film> film = Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_FILM_ID,
                    this::mapRowToFilmsWithGenre, id));
            if (film.isPresent()) {
                film.get().getLikes().addAll(getIdLikeOfFilm(id));
            }
            return film;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            List<Film> films = jdbcTemplate.queryForObject(SELECT_ALL_FILMS_GENRES, this::mapRowToIdFilmsGenre);
            return films;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }

    }

    @Override
    public List<Film> getTopFilms(int count) {
        try {
            List<Integer> topFilmsIds = jdbcTemplate.query(SELECT_TOP_FILM_ID, this::mapRowToInteger, count);
            if (topFilmsIds.isEmpty()) {
                return Collections.emptyList();
            }
            List<Film> films = jdbcTemplate.queryForObject(SELECT_TOP_FILM_GENRES, this::mapRowToIdFilmsGenre, count);
            ArrayList<Film> topFilms = new ArrayList<>();
            for (int i : topFilmsIds) {
                for (Film currentFilm : films) {
                    if (currentFilm.getId() == i) {
                        topFilms.add(currentFilm);
                        break;
                    }
                }
            }
            return topFilms;
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int deleteAllFilms() {
        return jdbcTemplate.update(DELETE_ALL_FILM);
    }

    @Override
    public int addLikeFilm(int filmId, int userId) {
        return jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public int deleteLikeFilm(int filmId, int userId) {
        return jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    private List<Genre> getAllGenreByFilmId(int id) {
        return jdbcTemplate.query(SELECT_GENRES_FILM, this::mapRowToIdGenre, id);
    }

    private List<Integer> getIdLikeOfFilm(int id) {
        return jdbcTemplate.query(SELECT_LIKES_FILM, this::mapRowToInteger, id);
    }

    private void insertGenresFilm(int filmId, List<Genre> genres) {
        jdbcTemplate.batchUpdate(INSERT_FILM_GENRE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, filmId);
                preparedStatement.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private Genre mapRowToIdGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        return genre;
    }

    private List<Film> mapRowToIdFilmsGenre(ResultSet resultSet, int rowNum) throws SQLException {
        HashMap<Integer, Film> filmsMap = new HashMap<>();
        do {
            Film film = new Film();
            film.setId(resultSet.getInt("id"));
            film.setName(resultSet.getString("name"));
            film.setDescription(resultSet.getString("description"));
            film.setDuration(resultSet.getInt("duration"));
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            film.setMpa(new Mpa(resultSet.getInt("rating_id"), resultSet.getString("mpa_name")));
            if (!filmsMap.containsKey(film.getId())) {
                filmsMap.put(film.getId(), film);
            }
            if (resultSet.getInt("genre_id") > 0) {
                Genre genre = new Genre();
                genre.setId(resultSet.getInt("genre_id"));
                genre.setName(resultSet.getString("genre_name"));
                filmsMap.get(film.getId()).getGenres().add(genre);
            }
            if (resultSet.getInt("like_id") > 0) {
                int userId = resultSet.getInt("like_id");
                filmsMap.get(film.getId()).getLikes().add(userId);
            }
        } while (resultSet.next());
        List<Film> films = new ArrayList<>();
        for (Film f : filmsMap.values()) {
            films.add(f);
        }
        films.sort(Comparator.comparing(Film::getId));
        return films;
    }

    private int mapRowToInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("id");
    }

    private Film mapRowToFilmsWithGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(resultSet.getInt("rating_id"), resultSet.getString("mpa_name")));
        do {
            if (resultSet.getInt("genre_id") > 0) {
                Genre genre = new Genre(resultSet.getInt("genre_id"),
                        resultSet.getString("genre_name"));
                film.getGenres().add(genre);
            }
        } while (resultSet.next());
        return film;
    }

}
