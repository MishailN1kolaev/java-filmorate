package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends IllegalArgumentException {
    public EntityNotFoundException(final String message) {
        super(message);
        log.error(message);
    }
}