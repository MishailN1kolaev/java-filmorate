package ru.yandex.practicum.filmorate.storage.film.impl;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikeComparator implements Comparator<Film> {

    @Override
    public int compare(Film f1, Film f2) {
        if (f1.getLikes().size() > f2.getLikes().size()) {
            return -1;
        } else if (f1.getLikes().size() < f2.getLikes().size()) {
            return 1;
        } else if (f1.getLikes().size() == f2.getLikes().size()) {
            if (f1.getId() > f2.getId()) {
                return 1;
            } else if (f1.getId() < f2.getId()) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

}
