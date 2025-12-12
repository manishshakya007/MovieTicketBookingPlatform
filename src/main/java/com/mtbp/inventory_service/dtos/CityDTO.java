package com.mtbp.inventory_service.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CityDTO(
        UUID id,
        String name
) { }
