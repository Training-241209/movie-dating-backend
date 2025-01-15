package com.moviedating.backend.Entity;

import com.moviedating.backend.Entity.enums.GenreType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "movie")
public class Movie {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Integer movieId;

    @Column(nullable = false)
    private String movieName;

    @Enumerated(EnumType.STRING)
    private GenreType movieGenre;
}
