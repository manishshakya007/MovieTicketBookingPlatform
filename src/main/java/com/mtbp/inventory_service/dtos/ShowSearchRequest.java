package com.mtbp.inventory_service.dtos;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ShowSearchRequest(
        String city,
        String movieId,
        LocalDate date
) { }
