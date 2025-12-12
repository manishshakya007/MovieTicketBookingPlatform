package com.mtbp.inventory_service.specifications;

import com.mtbp.inventory_service.entities.Seat;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SeatSpecifications {

    public static Specification<Seat> byId(String seatIdStr) {
        return (root, query, cb) -> {
            if (seatIdStr == null || seatIdStr.isBlank()) return null;
            UUID seatId = UUID.fromString(seatIdStr);
            return cb.equal(root.get("id"), seatId);
        };
    }

    public static Specification<Seat> byTheatreId(String theatreIdStr) {
        return (root, query, cb) -> {
            if (theatreIdStr == null || theatreIdStr.isBlank()) return null;
            UUID theatreId = UUID.fromString(theatreIdStr);
            return cb.equal(root.get("theatre").get("id"), theatreId);
        };
    }

    public static Specification<Seat> bySeatNumber(String seatNumberStr) {
        return (root, query, cb) -> {
            if (seatNumberStr == null || seatNumberStr.isBlank()) return null;
            UUID seatNumber = UUID.fromString(seatNumberStr);
            return cb.equal(root.get("seatNumber"), seatNumber);
        };
    }

    public static Specification<Seat> bySeatType(String seatTypeStr) {
        return (root, query, cb) -> {
            if (seatTypeStr == null || seatTypeStr.isBlank()) return null;
            UUID seatType = UUID.fromString(seatTypeStr);
            return cb.equal(root.get("seatType"), seatType);
        };
    }

    public static Specification<Seat> byAvailability(Boolean available) {
        return (root, query, cb) -> {
            if (available == null) return null;
            return cb.equal(root.get("available"), available);
        };
    }
}
