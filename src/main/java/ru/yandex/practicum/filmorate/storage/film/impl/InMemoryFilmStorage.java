package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;


@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id;
    private final Map<Integer, Film> films;
    private final List<Film> topFilms;
    private final FilmLikeComparator comporator;

    public InMemoryFilmStorage() {
        id = 0;
        films = new HashMap<>();
        topFilms = new ArrayList<Film>();
        comporator = new FilmLikeComparator();
    }

    @Override
    public Film addFilm(Film film) {
        id++;
        film.setId(id);
        films.put(id,film);
        topFilms.add(film);
        topFilms.sort(comporator);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            Film currentFilm = films.get(filmId);
            currentFilm.setName(film.getName());
            currentFilm.setDescription(film.getDescription());
            currentFilm.setReleaseDate(film.getReleaseDate());
            currentFilm.setDuration(film.getDuration());
            topFilms.sort(comporator);
            return Optional.of(currentFilm);
        }
        return Optional.empty();
    }

    @Override
    public int deleteFilmById(int id) {
        if (films.containsKey(id)) {
            Film currentFilm = films.get(id);
            topFilms.remove(currentFilm);
            films.remove(id);
            return id;
        }
        return 0;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public int deleteAllFilms() {
        topFilms.clear();
        films.clear();
        if ((films.size() > 0) || (topFilms.size() > 0)) {
            return 0;
        }
        return 1;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        if (count < topFilms.size()) {
            return new ArrayList<>(topFilms.subList(0, count));
        }
        return new ArrayList<>(topFilms);
    }

    @Override
    public int addLikeFilm(int filmId, int userId) {
        return 0;
    }

    @Override
    public int deleteLikeFilm(int filmId, int userId) {
        return 0;
    }

}
