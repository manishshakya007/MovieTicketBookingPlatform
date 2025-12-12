package com.mtbp.inventory_service.services;

import com.mtbp.inventory_service.dtos.MovieDTO;

import java.util.Optional;

public interface MovieService {
    Optional<MovieDTO> getMovieByTitle(String movieTitle);
}
