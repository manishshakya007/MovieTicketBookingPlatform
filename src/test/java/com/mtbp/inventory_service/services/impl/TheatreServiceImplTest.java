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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheatreServiceImplTest {

    private TheatreRepository theatreRepository;
    private CityRepository cityRepository;
    private TheatreMapper theatreMapper;
    private SeatRepository seatRepository;

    private TheatreServiceImpl theatreService;

    @BeforeEach
    void setup() {
        theatreRepository = mock(TheatreRepository.class);
        cityRepository = mock(CityRepository.class);
        theatreMapper = mock(TheatreMapper.class);
        seatRepository = mock(SeatRepository.class);

        theatreService = new TheatreServiceImpl(
                theatreRepository, cityRepository, theatreMapper, seatRepository
        );
    }

    // ---------------------------------------------------
    // saveTheatre()
    // ---------------------------------------------------

    @Test
    void testSaveTheatre_WithCityId() {
        UUID cityId = UUID.randomUUID();

        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(100)
                .cityId(cityId.toString())
                .cityName("Mumbai")
                .build();

        City city = new City();
        Theatre theatre = new Theatre();
        Theatre savedTheatre = new Theatre();

        TheatreDTO mappedResponse = dto;

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(theatreMapper.toEntity(dto)).thenReturn(theatre);
        when(theatreRepository.save(theatre)).thenReturn(savedTheatre);
        when(theatreMapper.toDTO(savedTheatre)).thenReturn(mappedResponse);

        TheatreDTO result = theatreService.saveTheatre(dto);

        assertNotNull(result);
        verify(seatRepository).saveAll(anyList());
    }

    @Test
    void testSaveTheatre_CityNotFoundById() {
        UUID cityId = UUID.randomUUID();

        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(100)
                .cityId(cityId.toString())
                .cityName("Mumbai")
                .build();

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> theatreService.saveTheatre(dto)
        );
    }

    @Test
    void testSaveTheatre_WithCityName() {
        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(100)
                .cityName("Mumbai")
                .build();

        City city = new City();
        Theatre theatre = new Theatre();
        Theatre savedTheatre = new Theatre();

        TheatreDTO mappedResponse = dto;

        when(cityRepository.findByName("Mumbai")).thenReturn(Optional.of(city));
        when(theatreMapper.toEntity(dto)).thenReturn(theatre);
        when(theatreRepository.save(theatre)).thenReturn(savedTheatre);
        when(theatreMapper.toDTO(savedTheatre)).thenReturn(mappedResponse);

        TheatreDTO result = theatreService.saveTheatre(dto);

        assertNotNull(result);
        verify(seatRepository).saveAll(anyList());
    }

    @Test
    void testSaveTheatre_CityNotFoundByName() {
        TheatreDTO dto = TheatreDTO.builder()
                .name("PVR")
                .address("Andheri")
                .totalSeats(100)
                .cityName("Unknown")
                .build();

        when(cityRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> theatreService.saveTheatre(dto)
        );
    }

    // ---------------------------------------------------
    // prepareSeatsForTheatre()
    // ---------------------------------------------------

    @Test
    void testPrepareSeatsForTheatre() {
        Theatre theatre = new Theatre();

        List<Seat> seats = theatreService.prepareSeatsForTheatre(theatre, 100);

        assertEquals(100, seats.size());
        assertEquals("VIP", seats.get(0).getSeatType());
    }

    // ---------------------------------------------------
    // patchTheatre()
    // ---------------------------------------------------

    @Test
    void testPatchTheatre_ValidUpdate() {
        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(50)
                .cityName("Mumbai")
                .build();

        Map<String, Object> updates = new HashMap<>();
        updates.put("totalSeats", 120);

        TheatreDTO updatedDto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(120)
                .cityName("Mumbai")
                .build();

        when(cityRepository.findByName(anyString())).thenReturn(Optional.of(new City()));
        when(theatreMapper.toEntity(any())).thenReturn(new Theatre());
        when(theatreRepository.save(any())).thenReturn(new Theatre());
        when(theatreMapper.toDTO(any())).thenReturn(updatedDto);

        TheatreDTO result = theatreService.patchTheatre(dto, updates);

        assertEquals(120, result.totalSeats());
    }

    @Test
    void testPatchTheatre_InvalidField() {
        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .totalSeats(50)
                .build();

        Map<String, Object> updates = Map.of("invalidField", "value");

        assertThrows(BadRequestException.class,
                () -> theatreService.patchTheatre(dto, updates)
        );
    }

    // ---------------------------------------------------
    // getAllTheatres()
    // ---------------------------------------------------

    @Test
    void testGetAllTheatres() {
        Theatre theatre = new Theatre();

        Page<Theatre> page = new PageImpl<>(
                List.of(theatre),
                PageRequest.of(0, 10),
                1
        );

        when(theatreRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(theatreMapper.toDTOList(anyList())).thenReturn(List.of(
                TheatreDTO.builder().id("1").name("PVR").address("Andheri").totalSeats(100).cityName("Mumbai").build()
        ));

        PageResponse<TheatreDTO> response = theatreService.getAllTheatres(0, 10);

        assertEquals(1, response.getTotalElements());
    }

    // ---------------------------------------------------
    // getTheatreById()
    // ---------------------------------------------------

    @Test
    void testGetTheatreById() {
        UUID id = UUID.randomUUID();
        Theatre theatre = new Theatre();

        TheatreDTO dto = TheatreDTO.builder()
                .id(id.toString())
                .name("PVR")
                .address("Andheri")
                .totalSeats(100)
                .build();

        when(theatreRepository.findById(id)).thenReturn(Optional.of(theatre));
        when(theatreMapper.toDTO(theatre)).thenReturn(dto);

        Optional<TheatreDTO> result = theatreService.getTheatreById(id.toString());

        assertTrue(result.isPresent());
        assertEquals(id.toString(), result.get().id());
    }

    // ---------------------------------------------------
    // getTheatresByCity()
    // ---------------------------------------------------

    @Test
    void testGetTheatresByCity() {
        City city = new City();
        Theatre theatre = new Theatre();

        TheatreDTO dto = TheatreDTO.builder()
                .id("1")
                .name("PVR")
                .address("Andheri")
                .cityName("Mumbai")
                .build();

        when(cityRepository.findByName("Mumbai")).thenReturn(Optional.of(city));
        when(theatreRepository.findByCity(city)).thenReturn(List.of(theatre));
        when(theatreMapper.toDTOList(anyList())).thenReturn(List.of(dto));

        List<TheatreDTO> result = theatreService.getTheatresByCity("Mumbai");

        assertEquals(1, result.size());
        assertEquals("PVR", result.get(0).name());
    }

    @Test
    void testGetTheatresByCity_NotFound() {
        when(cityRepository.findByName("Unknown")).thenReturn(Optional.empty());

        List<TheatreDTO> result = theatreService.getTheatresByCity("Unknown");

        assertTrue(result.isEmpty());
    }
}