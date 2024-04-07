package ru.yandex.practicum.filmorate.exeption;

public class DataBaseExeption extends RuntimeException {
    public DataBaseExeption(String message) {
        super(message);
    }
}
