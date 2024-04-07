package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    Mpa addMpa(Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();

    int deleteMpaById(int id);

    int deleteAllMpa();
}
