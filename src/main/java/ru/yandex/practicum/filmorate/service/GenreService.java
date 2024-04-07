package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre addGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    int deleteGenreById(int id);

    int deleteAllGenres();
}
