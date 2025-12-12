package com.mtbp.inventory_service.dtos;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record BookingDTO(
        String id,
        String customerId,
        String customerName,
        UUID showId,
        LocalDateTime showDateTime,
        LocalDateTime bookingTime,
        Double totalAmount,
        String status,  // BOOKED, CANCELLED
        List<String> bookingDetailIds
) {
}
