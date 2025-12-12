package com.mtbp.inventory_service.services.impl;

import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.SeatDTO;
import com.mtbp.inventory_service.entities.Seat;
import com.mtbp.inventory_service.entities.Show;
import com.mtbp.inventory_service.entities.Theatre;
import com.mtbp.inventory_service.exceptions.ResourceNotFoundException;
import com.mtbp.inventory_service.mapper.SeatMapper;
import com.mtbp.inventory_service.repositories.SeatRepository;
import com.mtbp.inventory_service.repositories.ShowRepository;
import com.mtbp.inventory_service.repositories.TheatreRepository;
import com.mtbp.inventory_service.services.SeatService;
import com.mtbp.inventory_service.specifications.SeatSpecifications;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final TheatreRepository theatreRepository;
    private final SeatMapper seatMapper;
    private final ShowRepository showRepository;

    public SeatServiceImpl(SeatRepository seatRepository, TheatreRepository theatreRepository, SeatMapper seatMapper, ShowRepository showRepository) {
        this.seatRepository = seatRepository;
        this.theatreRepository = theatreRepository;
        this.seatMapper = seatMapper;
        this.showRepository = showRepository;
    }

    public SeatDTO addSeat(SeatDTO seatDTO) {
        Seat seat = seatMapper.toEntity(seatDTO);

        Theatre theatre = theatreRepository.findById(UUID.fromString(seatDTO.theatreId()))
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + seatDTO.theatreId()));
        seat.setTheatre(theatre);

        Show show = showRepository.findById(seatDTO.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + seatDTO.showId()));
        seat.setShow(show);

        Seat savedSeat = seatRepository.save(seat);
        return seatMapper.toDTO(savedSeat);
    }

    public void deleteSeat(String seatId, String theatreId, String seatNumber) {
        Seat seat = null;
        if (seatId != null) {
            seat = seatRepository.findById(UUID.fromString(seatId))
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));
        } else {
            seat = seatRepository.findByTheatreIdAndSeatNumber(UUID.fromString(theatreId), seatNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat Number: " + seatNumber + " :does not belong to theatre with id: " + theatreId));
        }
        seatRepository.delete(seat);
    }

    public SeatDTO patchSeat(String theatreId, String seatId, Map<String, Object> updates) {
        Seat seat = seatRepository.findById(UUID.fromString(seatId))
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

        if (!seat.getTheatre().getId().equals(theatreId)) {
            throw new ResourceNotFoundException("Seat does not belong to theatre with id: " + theatreId);
        }

        updates.forEach((key, value) -> {
            try {
                Field field = Seat.class.getDeclaredField(key);
                field.setAccessible(true);

                if (field.getType().equals(Boolean.class) && value instanceof Boolean) {
                    field.set(seat, value);
                } else if (field.getType().equals(String.class) && value instanceof String) {
                    field.set(seat, value);
                } else {
                    throw new IllegalArgumentException("Invalid value for field: " + key);
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ResourceNotFoundException("Invalid field: " + key);
            }
        });

        Seat updatedSeat = seatRepository.save(seat);
        return seatMapper.toDTO(updatedSeat);
    }

    public PageResponse<SeatDTO> getSeats(String seatId, String theatreId, String seatNumber, int page, int size) {
        List<SeatDTO> filteredSeats;

        if (theatreId != null && seatId != null) {
            filteredSeats = Collections.singletonList(getSeatByIdAndTheatre(seatId, theatreId));
        } else if (theatreId != null && seatNumber != null) {
            filteredSeats = getSeatsByTheatreIdAndSeatNumber(theatreId, seatNumber);
        } else if (theatreId != null) {
            filteredSeats = getSeatsByTheatre(theatreId);
        } else if (seatId != null) {
            filteredSeats = Collections.singletonList(getSeatById(seatId));
        } else if (seatNumber != null) {
            filteredSeats = getSeatsBySeatNumber(seatNumber);
        } else {
            filteredSeats = getAllSeats();
        }

        int start = page * size;
        int end = Math.min(start + size, filteredSeats.size());
        List<SeatDTO> pagedList = (start > filteredSeats.size()) ? List.of() : filteredSeats.subList(start, end);

        int totalPages = (int) Math.ceil((double) filteredSeats.size() / size);

        return new PageResponse<>(
                pagedList,
                page,
                size,
                filteredSeats.size(),
                totalPages,
                page == totalPages - 1 || totalPages == 0
        );
    }

    public SeatDTO getSeatByIdAndTheatre(String seatId, String theatreId) {
        Seat seat = seatRepository.findByIdAndTheatreId(UUID.fromString(seatId), UUID.fromString(theatreId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found with ID " + seatId + " in Theatre " + theatreId));
        return seatMapper.toDTO(seat);
    }

    public List<SeatDTO> getSeatsByTheatre(String theatreId) {
        List<Seat> seats = seatRepository.findByTheatreId(UUID.fromString(theatreId));
        return seatMapper.toDTOList(seats);
    }

    public SeatDTO getSeatById(String seatId) {
        Seat seat = seatRepository.findById(UUID.fromString(seatId))
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID " + seatId));
        return seatMapper.toDTO(seat);
    }

    public List<SeatDTO> getAllSeats() {
        return seatMapper.toDTOList(seatRepository.findAll());
    }

    public List<SeatDTO> getSeatsByTheatreIdAndSeatNumber(String theatreId, String seatNumber) {
        return seatMapper.toDTOList(seatRepository.findAll());
    }

    public List<SeatDTO> getSeatsBySeatNumber(String seatNumber) {
        List<Seat> seats = seatRepository.findBySeatNumber(seatNumber);
        return seatMapper.toDTOList(seats);
    }

    public List<SeatDTO> getSeatsByFilter(Map<String, Object> filters) {

        Specification<Seat> spec = null;

        if (filters.containsKey("seatId")) {
            String seatId = (String) filters.get("seatId");
            spec = SeatSpecifications.byId(seatId);
        }
        if (filters.containsKey("theatreId")) {
            String theatreId = (String) filters.get("theatreId");
            spec = (spec == null ? SeatSpecifications.byTheatreId(theatreId)
                    : spec.and(SeatSpecifications.byTheatreId((String) filters.get("theatreId"))));
        }
        if (filters.containsKey("seatNumber")) {
            spec = (spec == null ? SeatSpecifications.bySeatNumber((String) filters.get("seatNumber"))
                    : spec.and(SeatSpecifications.bySeatNumber((String) filters.get("seatNumber"))));
        }
        if (filters.containsKey("seatType")) {
            spec = (spec == null ? SeatSpecifications.bySeatType((String) filters.get("seatType"))
                    : spec.and(SeatSpecifications.bySeatType((String) filters.get("seatType"))));
        }
        if (filters.containsKey("available")) {
            spec = (spec == null ? SeatSpecifications.byAvailability((Boolean) filters.get("available"))
                    : spec.and(SeatSpecifications.byAvailability((Boolean) filters.get("available"))));
        }

        List<Seat> seats = seatRepository.findAll((Sort) spec);
        return seatMapper.toDTOList(seats);
    }

    @Transactional
    public void assignShowToSeats(Show show) {

        UUID theatreId = show.getTheatre().getId();

        List<Seat> seats = seatRepository.findByTheatreId(theatreId);

        for (Seat seat : seats) {
            seat.setShow(show);  // JPA puts correct FK
        }

        seatRepository.saveAll(seats);
    }

    public int updateShowForTheatre(String theatreId, String showId) {
        Show show = showRepository.findById(UUID.fromString(showId))
                .orElseThrow(() -> new RuntimeException("Show not found"));
        int updatedRows = seatRepository.updateShowIdByTheatreId(UUID.fromString(theatreId), show.getId());
        log.debug("Updated rows: {}", updatedRows);
        return updatedRows;
    }

}
