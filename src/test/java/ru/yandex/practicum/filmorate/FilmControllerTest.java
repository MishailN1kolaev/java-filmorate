package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class FilmControllerTest {
    private FilmStorage storage = new InMemoryFilmStorage();
    private UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(userStorage);
    private FilmService service = new FilmService(storage);
    private FilmController controller = new FilmController(service);
    private final Film film = new Film(1L, "TITANIK", "Just for cry",
            LocalDate.of(2020, 2, 2), 120, new HashSet<>());
    private final Film updatedFilm = new Film(1L, "Movie",
            "so so not bad",
            LocalDate.of(2020, 2, 2), 120, new HashSet<>());
    private final Film noNamedFilm = new Film(1L, "", "kek",
            LocalDate.of(2020, 2, 2), 120, new HashSet<>());
    private final Film longDescpriptionFilm = new Film(1L, "Lord of the Ring",
            "",
            LocalDate.of(2020, 2, 2), 120, new HashSet<>());
    private final Film negativeDurationFilm = new Film(1L, "BYMER",
            "EMA",
            LocalDate.of(2020, 2, 2), -15, new HashSet<>());
    private final User user = new User(2L, "MIKKI@ya.ru", "MIKKI", "Mishail",
            LocalDate.of(1997, 3, 5), new HashSet<>());

    @AfterEach
    public void afterEach() {
        storage.deleteFilms();
    }

    @Test
    void createFilm_shouldAddAMovie() {
        controller.createFilm(film);

        Assertions.assertEquals(1, controller.getFilms().size());
    }

    @Test
    void updateFilm_shouldUpdateMovieData() {
        controller.createFilm(film);
        controller.updateFilm(updatedFilm);

        Assertions.assertEquals("so so not bad", updatedFilm.getDescription());
        Assertions.assertEquals(1, controller.getFilms().size());
    }

    @Test
    void getFilmById_shouldReturnAMovieWithIdOne() {
        controller.createFilm(film);
        Film thisFilm = controller.getFilmById(film.getId());

        Assertions.assertEquals(1, thisFilm.getId());
    }

    @Test
    void createFilm_shouldNotAddAMovieWithAnEmptyName() {
        Assertions.assertThrows(ValidationException.class, () -> controller.createFilm(noNamedFilm));
        Assertions.assertEquals(0, controller.getFilms().size());
    }

    @Test
    void createFilm_shouldNotAddAMovieWithDescriptionMoreThan200() {
        Assertions.assertThrows(ValidationException.class, () -> controller.createFilm(longDescpriptionFilm));
        Assertions.assertEquals(0, controller.getFilms().size());
    }

    @Test
    void createFilm_shouldNotAddAMovieWithDateReleaseLessThan1895() {
        film.setReleaseDate(LocalDate.of(1891, 2, 2));

        Assertions.assertThrows(ValidationException.class, () -> controller.createFilm(film));
        Assertions.assertEquals(0, controller.getFilms().size());
    }

    @Test
    void createFilm_shouldNotAddAMovieIfDurationIsLessThan0() {
        Assertions.assertThrows(ValidationException.class, () -> controller.createFilm(negativeDurationFilm));
        Assertions.assertEquals(0, controller.getFilms().size());
    }

    @Test
    void likeAMovie_shouldAddALikeToAMovie() {
        userStorage.createUser(user);
        controller.createFilm(film);
        controller.likeAMovie(film.getId(), user.getId());

        Assertions.assertTrue(film.getLikesQuantity() != 0);
    }

    @Test
    void removeLike_shouldRemoveLikeFromAMovie() {
        userStorage.createUser(user);
        controller.createFilm(film);
        controller.likeAMovie(film.getId(), user.getId());
        controller.removeLike(film.getId(), user.getId());

        Assertions.assertEquals(0, film.getLikesQuantity());
    }

    @Test
    void getPopularMovies_shouldReturnListOfPopularMovies() {
        userStorage.createUser(user);
        controller.createFilm(film);
        controller.likeAMovie(film.getId(), user.getId());
        List<Film> popularMoviesList = service.getPopularMovies(1);

        Assertions.assertEquals(1, popularMoviesList.size());
    }
}