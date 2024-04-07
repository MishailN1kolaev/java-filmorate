package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

}
