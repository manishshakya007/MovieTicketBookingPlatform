package com.mtbp.inventory_service.services;

import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.TheatreDTO;
import com.mtbp.inventory_service.entities.Seat;
import com.mtbp.inventory_service.entities.Theatre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TheatreService {
    TheatreDTO saveTheatre(TheatreDTO theatreDTO);

    Optional<TheatreDTO> getTheatreById(String id);

    TheatreDTO patchTheatre(TheatreDTO theatre, Map<String, Object> updates);

    PageResponse<TheatreDTO> getAllTheatres(int page, int size);

    List<TheatreDTO> getTheatresByCity(String cityName);

    List<Seat> prepareSeatsForTheatre(Theatre theatre, int totalSeats);
}
