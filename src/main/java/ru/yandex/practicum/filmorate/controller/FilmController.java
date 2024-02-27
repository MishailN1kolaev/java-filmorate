package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.Exception;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @ResponseBody
    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        filmValidation(film);
        films.put(film.getId(), film);
        log.info("'{}' фильм был добавлен, идентификатор'{}'", film.getName(), film.getId());
        return film;
    }

    @ResponseBody
    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        filmValidation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("'{}' фильм был обновлен, идентификатор '{}'", film.getName(), film.getId());
        } else {
            throw new Exception("Попытка обновить несуществующий фильм");
        }
        return film;
    }

    @ResponseBody
    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Количество фильмов: '{}'", films.size());
        return new ArrayList<>(films.values());
    }

    private void filmValidation(Film film) {

        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new Exception("Попытка задать пустое название фильма");
        }
        if (film.getDescription().length() > 200 || film.getDescription().length() == 0) {
            throw new Exception("Описание больше 200 символов или пустое");
        }
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new Exception("Неверная дата релиза");
        }
        if (film.getDuration() <= 0) {
            throw new Exception("Установка продолжительности меньше нуля");
        }
        if (film.getId() <= 0) {
            film.setId(++id);
            log.info("Неверный идентификатор фильма '{}", film.getId());
        }
    }
}