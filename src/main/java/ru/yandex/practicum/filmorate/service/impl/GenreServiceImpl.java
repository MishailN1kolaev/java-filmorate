package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;


@Service
public class GenreServiceImpl implements GenreService {
    private GenreStorage storage;

    @Autowired
    public GenreServiceImpl(GenreStorage storage) {
        this.storage = storage;
    }

    @Override
    public Genre addGenre(Genre genre) {
        return null;
    }

    @Override
    public Genre updateGenre(Genre genre) {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return storage.getGenreById(id).orElseThrow(() -> new IncorrectIdException("wrong id"));
    }

    @Override
    public List<Genre> getAllGenres() {
        return storage.getAllGenres();
    }

    @Override
    public int deleteGenreById(int id) {
        return 0;
    }

    @Override
    public int deleteAllGenres() {
        return 0;
    }
}
