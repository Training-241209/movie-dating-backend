package com.moviedating.backend.Entity;
import jakarta.persistence.*;
import com.moviedating.backend.Entity.enums.GenreType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="genre")
public class Genre {

    @Id
    private Integer genreId;

    @Enumerated(EnumType.STRING)
    @Column(unique = true , nullable = false)
    private GenreType movieGenre;
}
