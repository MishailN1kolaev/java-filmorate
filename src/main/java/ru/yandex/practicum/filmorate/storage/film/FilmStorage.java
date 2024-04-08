package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    int deleteFilmById(int id);

    Optional<Film> getFilmById(int id);

    List<Film> getAllFilms();

    List<Film> getTopFilms(int count);

    int deleteAllFilms();

    int addLikeFilm(int filmId, int userId);

    int deleteLikeFilm(int filmId, int userId);

}
