package com.moviedating.backend.dtos;
import lombok.Data;

@Data
public class FavoritesDTO {
    private Integer movieId;
    private Integer genreId;
}
