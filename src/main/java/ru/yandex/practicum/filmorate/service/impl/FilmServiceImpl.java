package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.validation.Validation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exeption.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final Validation validator;

    @Autowired
    public FilmServiceImpl(@Qualifier("FilmDbStorage") FilmStorage storage,
                            @Qualifier("UserDbStorage") UserStorage userStorage, Validation validator) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film, "POST");
        return storage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film, "PUT");
        Optional<Film> updFilm = storage.updateFilm(film);
        if (updFilm.isEmpty()) {
            throw new IncorrectIdException("wrong id");
        }
        return updFilm.get();
    }

    @Override
    public int deleteFilmById(int id) {
        return storage.deleteFilmById(id);
    }

    @Override
    public Film getFilmById(int id) {
        Film film = storage.getFilmById(id).orElseThrow(() -> new IncorrectIdException("wrong id"));
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    @Override
    public int deleteAllFilms() {
        int resultId = storage.deleteAllFilms();
        if (resultId == 0) {
            log.error("не получилось очистить таблицу фильмов: код ответа - '{}', ", resultId);
            throw new ValidationException("не получилось очистить таблицу фильмов");
        }
        log.error("все фильмы удалены: код ответа - '{}', ", resultId);
        return resultId;
    }

    @Override
    public List<Integer> addLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            log.error("неверный id  фильма или пользователя: фильм - '{}', пользователь - '{}'", idFilm, idUser);
            throw new IncorrectIdException("неверный id  фильма или пользователя");
        }
        film.get().getLikes().add(idUser);
        int i = storage.addLikeFilm(idFilm, idUser);
        if (i > 0) {
            return new ArrayList<>(film.get().getLikes());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Integer> deleteLike(int idFilm, int idUser) {
        final Optional<Film> film = storage.getFilmById(idFilm);
        final Optional<User> user = userStorage.getUserById(idUser);
        if (film.isEmpty() || user.isEmpty()) {
            log.error("неверный id  фильма или пользователя: фильм - '{}', пользователь - '{}'", idFilm, idUser);
            throw new IncorrectIdException("неверный id фильма или пользователя");
        }
        film.get().getLikes().remove(idUser);
        int i = storage.deleteLikeFilm(idFilm, idUser);
        if (i > 0) {
            return new ArrayList<>(film.get().getLikes());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return storage.getTopFilms(count);
    }

    private void validateFilm(Film film, String method) {
        String description = film.getDescription();
        if (!validator.isLengthOk(description)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("слишком длинное описание фильма");
        }
        LocalDate date = film.getReleaseDate();
        if (!validator.isDateFilmOk(date)) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("дата релиза некоректная");
        }
        if (film.getDuration() < 0) {
            log.error("Ошибка в данных запроса к эндпоинту:{} /films ', : '{}'", method, film);
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
    }
}
