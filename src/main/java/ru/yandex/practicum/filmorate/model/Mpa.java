package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@Data
public class Mpa {
    private int id;
    @NotBlank
    private String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
