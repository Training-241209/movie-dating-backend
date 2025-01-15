package com.moviedating.backend.Entity;

import org.hibernate.annotations.DynamicUpdate;
import com.moviedating.backend.Entity.Movie;
import com.moviedating.backend.Entity.enums.GenderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer accountId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // @OneToOne
    // @JoinColumn(name = "movieId", nullable = false)
    @Column(nullable = true)
    private Integer favoriteMovie;

    @Column(nullable = true)
    private Integer favoriteGenre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private GenderType Gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private GenderType genderPreference;

}
