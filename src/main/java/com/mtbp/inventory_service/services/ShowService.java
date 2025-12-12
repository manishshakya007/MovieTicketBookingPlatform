package com.mtbp.inventory_service.services;

import com.mtbp.inventory_service.dtos.MovieDTO;
import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.ShowDTO;
import com.mtbp.inventory_service.dtos.ShowResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShowService {
    ShowDTO saveShow(ShowDTO showDTO);

    PageResponse<ShowDTO> getShows(MovieDTO movieDto, String cityName, LocalDate date, int page, int size);

    Optional<ShowDTO> getShowById(String showId);

    ShowDTO patchShow(ShowDTO existingShow, Map<String, Object> updates);

    void deleteShow(String showId);

    List<ShowResponseDTO> getShowsByCityMovieAndDate(String city, String movieId, LocalDate date);
}
