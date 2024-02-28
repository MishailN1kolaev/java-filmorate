package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Exception extends IllegalArgumentException {
    public Exception(final String message) {
        log.error(message);
    }
}