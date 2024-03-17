package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id;

    public InMemoryFilmStorage() {
        id = 0L;
    }

    @Override
    public Film createFilm(Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("'{}' фильм был добавлен в библиотеку, идентификатор - '{}'", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            validate(film);
            films.put(film.getId(), film);
            log.info("'{}' фильм был обновлен в библиотеке, идентификатор - '{}'", film.getName(), film.getId());
            return film;
        } else {
            throw new EntityNotFoundException("Попытка обновить несуществующий фильм");
        }
    }

    @Override
    public void deleteFilms() {
        films.clear();
        log.info("Хранилище фильмов пусто");
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Попытка получить доступ к несуществующему фильму с идентификатором'" + id + "'");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getFilms() {
        log.info("Сейчас в библиотеке '{}' фильмов", films.size());
        return new ArrayList<>(films.values());
    }

    private void validate(Film film) {
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Неверная дата выпуска");
        }
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Попытка задать пустое название фильма");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Попытка установить продолжительность меньше нуля");
        }
        if (film.getDescription().length() > 200 || film.getDescription().length() == 0) {
            throw new ValidationException("Описание больше 200 символов или пусто");
        }
        if (film.getId() == null || film.getId() <= 0) {
            film.setId(++id);
            log.info("Идентификатор фильма был задан как '{}'", film.getId());
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
    }
}