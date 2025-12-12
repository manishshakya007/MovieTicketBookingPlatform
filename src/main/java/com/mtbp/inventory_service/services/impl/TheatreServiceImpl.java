package com.mtbp.inventory_service.services.impl;

import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.TheatreDTO;
import com.mtbp.inventory_service.entities.City;
import com.mtbp.inventory_service.entities.Seat;
import com.mtbp.inventory_service.entities.Theatre;
import com.mtbp.inventory_service.exceptions.BadRequestException;
import com.mtbp.inventory_service.exceptions.ResourceNotFoundException;
import com.mtbp.inventory_service.mapper.TheatreMapper;
import com.mtbp.inventory_service.repositories.CityRepository;
import com.mtbp.inventory_service.repositories.SeatRepository;
import com.mtbp.inventory_service.repositories.TheatreRepository;
import com.mtbp.inventory_service.services.TheatreService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
public class TheatreServiceImpl implements TheatreService {
    private final TheatreRepository theatreRepository;
    private final CityRepository cityRepository;
    private final TheatreMapper theatreMapper;
    private final SeatRepository seatRepository;

    public TheatreServiceImpl(TheatreRepository theatreRepository, CityRepository cityRepository,
                              TheatreMapper theatreMapper, SeatRepository seatRepository) {
        this.theatreRepository = theatreRepository;
        this.cityRepository = cityRepository;
        this.theatreMapper = theatreMapper;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public TheatreDTO saveTheatre(TheatreDTO theatreDTO) {
        City city = null;
        if (theatreDTO.cityId() != null) {
            city = cityRepository.findById(UUID.fromString(theatreDTO.cityId()))
                    .orElseThrow(() -> new ResourceNotFoundException("City not found: " + theatreDTO.cityName()));
        } else if (theatreDTO.cityName() != null) {
            city = cityRepository.findByName(theatreDTO.cityName())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found: " + theatreDTO.cityName()));
        }

        Theatre theatre = theatreMapper.toEntity(theatreDTO);
        theatre.setCity(city);

        Theatre savedTheatre = theatreRepository.save(theatre);

        List<Seat> seats = prepareSeatsForTheatre(savedTheatre, theatreDTO.totalSeats());

        seatRepository.saveAll(seats);

        return theatreMapper.toDTO(savedTheatre);
    }

    public List<Seat> prepareSeatsForTheatre(Theatre theatre, int totalSeats) {
        List<Seat> seats = new ArrayList<>();

        int vipCount = (int) Math.ceil(totalSeats * 0.10);
        int premiumCount = (int) Math.ceil(totalSeats * 0.40);
        int regularCount = totalSeats - vipCount - premiumCount;

        for (int i = 1; i <= vipCount; i++) {
            seats.add(new Seat(null, "V" + i, "VIP", true, theatre, null));
        }

        for (int i = 1; i <= premiumCount; i++) {
            seats.add(new Seat(null, "P" + i, "PREMIUM", true, theatre, null));
        }

        for (int i = 1; i <= regularCount; i++) {
            seats.add(new Seat(null, "R" + i, "REGULAR", true, theatre, null));
        }

        return seats;
    }

    /*public TheatreDTO patchTheatre(TheatreDTO theatreDTO, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            try {
                Field field = TheatreDTO.class.getDeclaredField(key);
                field.setAccessible(true);

                if (field.getType().equals(Integer.class) && value instanceof Number) {
                    field.set(theatreDTO, ((Number) value).intValue());
                } else {
                    field.set(theatreDTO, value);
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("Invalid Theatre field: {}", key);
                throw new BadRequestException("Invalid Theatre field: " + key);
            }
        });

        return saveTheatre(theatreDTO);
    }*/

    public TheatreDTO patchTheatre(TheatreDTO dto, Map<String, Object> updates) {

        TheatreDTO.TheatreDTOBuilder builder = dto.toBuilder();

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> builder.name((String) value);
                case "address" -> builder.address((String) value);
                case "totalSeats" -> builder.totalSeats((Integer) value);
                case "cityId" -> builder.cityId((String) value);
                case "cityName" -> builder.cityName((String) value);
                default -> throw new BadRequestException("Invalid Theatre field: " + key);
            }
        });

        TheatreDTO updated = builder.build();
        return saveTheatre(updated);
    }

    public PageResponse<TheatreDTO> getAllTheatres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Theatre> theatrePage = theatreRepository.findAll(pageable);

        List<TheatreDTO> theatreDTOs = theatreMapper.toDTOList(theatrePage.getContent());

        return new PageResponse<>(
                theatreDTOs,
                theatrePage.getNumber(),
                theatrePage.getSize(),
                theatrePage.getTotalElements(),
                theatrePage.getTotalPages(),
                theatrePage.isLast()
        );
    }

    public Optional<TheatreDTO> getTheatreById(String id) {
        return theatreRepository.findById(UUID.fromString(id))
                .map(theatreMapper::toDTO);
    }

    public List<TheatreDTO> getTheatresByCity(String cityName) {
        Optional<City> city = cityRepository.findByName(cityName);
        if (city.isEmpty()) return List.of();

        List<Theatre> theatres = theatreRepository.findByCity(city.get());
        return theatreMapper.toDTOList(theatres);
    }
}
