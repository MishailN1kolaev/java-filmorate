package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;


@Slf4j
@Validated
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос к эндпоинту: GET /films '");
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilms(@PathVariable int id) {
        log.info("Получен запрос к эндпоинту: GET /films/'{}' '", id);
        Film film = filmService.getFilmById(id);
        log.info("Ответ: GET /films/'{}' '", film);
        return film;
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Получен запрос к эндпоинту: GET /films/popular?count={} '", count);
        List<Film> films = filmService.getTopFilms(count);
        log.info("Ответ: GET /films/popular?count={} ' {}", count,films);
        return films;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody  Film film) {
        log.info("Получен запрос к эндпоинту: POST /films ', Строка параметров запроса: '{}'", film);
        Film newFilm = filmService.addFilm(film);
        log.info("Ответ на запрос к эндпоинту: POST /films ', : '{}'", film);
        return newFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("запрос к эндпоинту: PUT /films ', Строка параметров запроса: '{}'", film);
        Film updFilm = filmService.updateFilm(film);
        log.info("Ответ на запрос к эндпоинту: PUT /films ', '{}'", updFilm);
        return updFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("запрос к эндпоинту: PUT /films/{}/like/{} ', Строка параметров запроса: ", id, userId);
        filmService.addLike(id, userId);
        Film film = filmService.getFilmById(id);
        return film;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("запрос к эндпоинту: DELETE /films/'{}'/like/'{}' ', Строка параметров запроса: ", id, userId);
        filmService.deleteLike(id, userId);
        Film film = filmService.getFilmById(id);
        return film;
    }

    @DeleteMapping("/films")
    public void deleteAllFilms() {
        log.info("запрос к эндпоинту: DELETE /films ");
        filmService.deleteAllFilms();
    }

}