package ru.yandex.practicum.filmorate.exeption;

public class IncorrectIdException extends RuntimeException {
    public IncorrectIdException(String message) {
        super(message);
    }
}
