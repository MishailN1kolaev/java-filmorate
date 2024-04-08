package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@BeforeEach
	public void beforeEach() {
		filmStorage.deleteAllFilms();
		userStorage.deleteAllUsers();
	}

	@Test
	@DisplayName("Добавление пользователя")
	public void testAddUser() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userFromDb = userStorage.addUser(user);
		assertNotNull(userFromDb.getId(),"Пользователь не создался в БД");
		assertEquals(1, userFromDb.getId(),
				"Возвращается неверный id.");
	}

	@Test
	public void testUpdateUser() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userFromDb = userStorage.addUser(user);
		userFromDb.setName("Nick Name Updated");
		Optional<User> updateUser = userStorage.updateUser(userFromDb);
		assertNotNull(updateUser.get(),"Пользователь не вернулся");
		assertEquals(userFromDb.getId(), updateUser.get().getId(),
				"Возвращается неверный id.");
		assertEquals("Nick Name Updated", updateUser.get().getName(),
				"Имя не обновилось.");

	}

	@Test
	@DisplayName("Получение пользователя по айди")
	public void testGetUserById() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name Updated");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userDb = userStorage.addUser(user);
		Optional<User> userOptional = userStorage.getUserById(userDb.getId());
		assertNotNull(userOptional.get(),"Пользователь не вернулся");
		assertEquals(userDb.getId(), userOptional.get().getId(),
				"Возвращается неверный id.");
	}

	@Test
	public void testDeleteUserById() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name Updated");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userDb = userStorage.addUser(user);
		int i = userStorage.deleteUserById(userDb.getId());
		assertNotNull(i,"Удаление не произошло");
		assertEquals(1, i,"Удалилось неверное колличество записей");
	}

	@Test
	public void testGetAllUser() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick Name Updated");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		userStorage.addUser(user);
		List<User> users = userStorage.getAllUser();
		assertNotNull(users,"Записи не найдены");
		assertEquals(1, users.size(),"Неверное колличество записей");
	}

	@Test
	public void testDeleteAllUsers() {
		List<User> users = userStorage.getAllUser();
		int i = userStorage.deleteAllUsers();
		List<User> usersAfterDel = userStorage.getAllUser();
		assertNotNull(i,"Записи не удалены");
		assertEquals(0, usersAfterDel.size(),"Неверное колличество записей");
	}

	@Test
	public void testAddFriendById() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userOne = userStorage.addUser(user);
		user.setLogin("friend");
		user.setName("Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1996,8,20));
		User userFriend = userStorage.addUser(user);
		int i = userStorage.addFriendById(userOne.getId(), userFriend.getId());
		List<Integer> friendsId = userStorage.getAllIdFriends(userOne.getId());
		assertNotNull(i,"Запись не созадана");
		assertEquals(1, i,"Неверное колличество записей добавлено");
		assertNotNull(friendsId,"Друг не добавлен");
		assertEquals(1, friendsId.size(),"Неверное колличество друзей");
		assertEquals(userFriend.getId(), friendsId.get(0),"Неверный id  в друзьях");
	}

	@Test
	public void testDeleteFriendById() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userOne = userStorage.addUser(user);
		user.setLogin("friend");
		user.setName("Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1996,8,20));
		User userFriend = userStorage.addUser(user);
		userStorage.addFriendById(userOne.getId(), userFriend.getId());
		userStorage.deleteFriendById(userOne.getId(), userFriend.getId());
		List<Integer> friendsId = userStorage.getAllIdFriends(userOne.getId());
		assertEquals(0, friendsId.size(),"Неверное колличество");
	}


	@Test
	public void testGetAllIdFriends() {
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userOne = userStorage.addUser(user);
		user.setLogin("friend");
		user.setName("Name");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1996,8,20));
		User userFriend = userStorage.addUser(user);
		int i = userStorage.addFriendById(userOne.getId(), userFriend.getId());
		List<Integer> friendsId = userStorage.getAllIdFriends(userOne.getId());
		assertNotNull(friendsId,"Друг не добавлен");
		assertEquals(1, friendsId.size(),"Неверное колличество друзей");
		assertEquals(userFriend.getId(), friendsId.get(0),"Неверный id  в друзьях");
	}

	@Test
	public void testAddFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film movie = filmStorage.addFilm(film);
		assertNotNull(movie.getId(),"фильмов не добавился");
		assertEquals(1, movie.getId(),
				"неверный id фильма");
	}

	@Test
	public void testUpdateFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film movie = filmStorage.addFilm(film);
		movie.setName("nisi eiusmod Update");
		Optional<Film> movieUp = filmStorage.updateFilm(movie);
		assertNotNull(movieUp.get().getId(),"неверный id фильма");
		assertEquals("nisi eiusmod Update", movieUp.get().getName(),
				"у фильма не обновилось имя");
	}

	@Test
	public void testDeleteFilmById() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film movie = filmStorage.addFilm(film);
		int i = filmStorage.deleteFilmById(movie.getId());
		assertEquals(1, i, "удалено неверное количество фильмов");
	}

	@Test
	public void testGetFilmById() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film filmInDB = filmStorage.addFilm(film);
		Optional<Film> movie = filmStorage.getFilmById(filmInDB.getId());
		assertNotNull(movie.get().getId(),"фильм не найден");
		assertEquals(filmInDB.getId(), movie.get().getId(),
				"неверный id найденного фильма");
	}

	@Test
	public void testGetAllFilms() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		filmStorage.addFilm(film);
		List<Film> movies = filmStorage.getAllFilms();
		assertNotNull(movies,"фильмы не найдены");
		assertEquals(1, movies.size(),
				"неверное колличество найденных фильмов");
	}

	@Test
	public void testGetTopFilms() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film filmInDbOne = filmStorage.addFilm(film);
		Film filmTwo = new Film();
		filmTwo.setName("nisi eiusmod New");
		filmTwo.setDescription("adipisicing");
		filmTwo.setReleaseDate(LocalDate.of(1967,03,25));
		filmTwo.setDuration(100);
		filmTwo.setMpa(mpa);
		Film filmInDbTwo = filmStorage.addFilm(filmTwo);
		List<Film> movies = filmStorage.getTopFilms(1);
		assertNotNull(movies,"фильмы не найдены");
		assertEquals(1, movies.size(),
				"неверное колличество найденных фильмов");
		assertEquals(filmInDbOne.getId(), movies.get(0).getId(),
				"неверный id топ фильма");
	}

	@Test
	public void testDeleteAllFilms() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		filmStorage.addFilm(film);
		filmStorage.deleteAllFilms();
		List<Film> moviesTop = filmStorage.getTopFilms(1);
		List<Film> moviesAll = filmStorage.getAllFilms();
		assertEquals(0, moviesTop.size(),
				"топ фильмы не удалены");
		assertEquals(0, moviesAll.size(),
				"фильмы не удалены");
	}

	@Test
	public void testAddLikeFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film movie = filmStorage.addFilm(film);
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userOne = userStorage.addUser(user);
		int i = filmStorage.addLikeFilm(movie.getId(), userOne.getId());
		Optional<Film> savedMovie = filmStorage.getFilmById(movie.getId());
		assertNotNull(savedMovie.get().getLikes(),
				"Лайк не добавился");
		assertEquals(1, savedMovie.get().getLikes().size(),
				"Неверное колличество лайков");
	}

	@Test
	public void testDeleteLikeFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("nisi eiusmod");
		film.setDescription("adipisicing");
		film.setReleaseDate(LocalDate.of(1967,03,25));
		film.setDuration(100);
		film.setMpa(mpa);
		Film movie = filmStorage.addFilm(film);
		User user = new User();
		user.setLogin("dolore");
		user.setName("Nick");
		user.setEmail("mail@mail.ru");
		user.setBirthday(LocalDate.of(1946,8,20));
		User userOne = userStorage.addUser(user);
		filmStorage.addLikeFilm(movie.getId(), userOne.getId());
		int i = filmStorage.deleteLikeFilm(movie.getId(), userOne.getId());
		Optional<Film> savedMovie = filmStorage.getFilmById(movie.getId());
		assertEquals(1, i,"Удалилось неверное количество Лайков");
		assertEquals(0, savedMovie.get().getLikes().size(),
				"Неверное колличество лайков");
	}
}
