package com.mtbp.inventory_service.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record MovieDTO(
        UUID id,
        String title,
        String genre,
        Integer durationMinutes,
        String language,
        LocalDate releaseDate,
        String rating
) { }
