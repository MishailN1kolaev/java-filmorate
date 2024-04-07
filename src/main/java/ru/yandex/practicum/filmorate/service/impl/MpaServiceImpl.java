package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaServiceImpl implements MpaService {
    private MpaStorage storage;

    @Autowired
    public MpaServiceImpl(MpaStorage storage) {
        this.storage = storage;
    }

    @Override
    public Mpa addMpa(Mpa mpa) {
        return null;
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        return null;
    }

    @Override
    public Mpa getMpaById(int id) {
        Mpa mpa = storage.getMpaById(id).orElseThrow(() -> new IncorrectIdException("wrong id"));
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpa() {
        return storage.getAllMpa();
    }

    @Override
    public int deleteMpaById(int id) {
        return 0;
    }

    @Override
    public int deleteAllMpa() {
        return 0;
    }
}
