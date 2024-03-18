package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void like(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Попытка получить доступ к несуществующему фильму с идентификатором '" + filmId + "'");
        }
        film.addLike(userId);
        log.info("'{}' понравился фильм '{}'", userId, filmId);
    }

    public void dislike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Попытка получить доступ к несуществующему фильму с идентификатором '" + filmId + "'");
        }
        film.removeLike(userId);
        log.info("'{}' фильм не понравился '{}'", userId, filmId);
    }

    public List<Film> getPopularMovies(int count) {
        log.info("Получаем список наиболее понравившихся фильмов");
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getLikesQuantity).reversed())
                .limit(count).collect(Collectors.toList());
    }
}