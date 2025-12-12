package com.mtbp.inventory_service.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowDTO(
        String id,
        String movieId,
        String movieTitle,
        String theatreId,
        String theatreName,
        LocalDate showDate,
        LocalTime startTime,
        LocalTime endTime,
        Double pricePerTicket,
        String showType
) {
}
