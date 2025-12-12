package com.mtbp.inventory_service.dtos;

import lombok.Builder;

@Builder(toBuilder = true)
public record TheatreDTO(
        String id,
        String name,
        String address,
        Integer totalSeats,
        String cityId,
        String cityName
) {
}
