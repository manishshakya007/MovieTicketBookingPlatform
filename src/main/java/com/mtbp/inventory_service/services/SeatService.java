package com.mtbp.inventory_service.services;

import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.SeatDTO;
import com.mtbp.inventory_service.entities.Show;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SeatService {
    int updateShowForTheatre(String theatreId, String showId);

    SeatDTO addSeat(SeatDTO seatDTO);

    void deleteSeat(String seatId, String theatreId, String seatNumber);

    SeatDTO patchSeat(String theatreId, String seatId, Map<String, Object> updates);

    PageResponse<SeatDTO> getSeats(String seatId, String theatreId, String seatNumber, int page, int size);

    List<SeatDTO> getSeatsByFilter(Map<String, Object> filters);

    public void assignShowToSeats(Show show);
}
