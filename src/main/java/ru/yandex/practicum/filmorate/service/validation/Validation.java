package ru.yandex.practicum.filmorate.service.validation;

import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class Validation {
    private static final int FILM_DESCRIPTION_LENGTH = 200;
    private static final int START_FILM_YEAR = 1895;
    private static final int START_FILM_MONTH = 12;
    private static final int START_FILM_DAY = 28;

    public boolean isLengthOk(String string) {
        if ((string == null) || string.isBlank()) {
            return true;
        }
        if (string.length() > FILM_DESCRIPTION_LENGTH) {
            return false;
        }
        return true;
    }

    public boolean isDateFilmOk(LocalDate date) {
        LocalDate startDate = LocalDate.of(START_FILM_YEAR, START_FILM_MONTH, START_FILM_DAY);
        if (date == null) {
           return true;
       }
        return startDate.isBefore(date);
    }

    public boolean isDateUserOk(LocalDate date) {
        LocalDate now = LocalDate.now();
        return now.isAfter(date);
    }

    public boolean isHasEmailSymbol(String email) {
        return email.contains("@");
    }

    public boolean isHasSpaceSymbol(String string) {
        if ((string == null) || string.isBlank()) {
            return true;
        }
        return string.contains(" ");
    }
}
