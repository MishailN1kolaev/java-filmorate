package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@Data
public class Genre {
    private int id;
    @NotBlank
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
