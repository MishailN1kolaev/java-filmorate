package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    int deleteFilmById(int id);

    int deleteAllFilms();

    Film getFilmById(int id);

    List<Film> getAllFilms();

    List<Integer> addLike(int idFilm, int idUser);

    List<Integer> deleteLike(int idFilm, int idUser);

    List<Film> getTopFilms(int count);

}
