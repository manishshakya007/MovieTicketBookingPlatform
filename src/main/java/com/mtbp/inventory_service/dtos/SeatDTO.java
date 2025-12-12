package com.mtbp.inventory_service.dtos;

import java.util.UUID;

public record SeatDTO(
        UUID id,
        String seatNumber,   // Example: R1, P1, V1
        String seatType,     // REGULAR, PREMIUM, VIP
        Boolean available,
        String theatreId,
        String theatreName,
        UUID showId
) {
}
