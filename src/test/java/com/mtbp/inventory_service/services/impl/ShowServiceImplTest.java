package com.mtbp.inventory_service.services.impl;

import com.mtbp.inventory_service.dtos.MovieDTO;
import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.ShowDTO;
import com.mtbp.inventory_service.dtos.ShowResponseDTO;
import com.mtbp.inventory_service.entities.Movie;
import com.mtbp.inventory_service.entities.Show;
import com.mtbp.inventory_service.entities.Theatre;
import com.mtbp.inventory_service.exceptions.DuplicateShowException;
import com.mtbp.inventory_service.mapper.ShowMapper;
import com.mtbp.inventory_service.repositories.MovieRepository;
import com.mtbp.inventory_service.repositories.ShowRepository;
import com.mtbp.inventory_service.repositories.TheatreRepository;
import com.mtbp.inventory_service.services.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowServiceImplTest {
    @Mock
    private ShowRepository showRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private TheatreRepository theatreRepository;
    @Mock private SeatService seatService;
    @Mock private ShowMapper showMapper;

    @InjectMocks
    private ShowServiceImpl service;

    private Show show;
    private ShowDTO showDTO;
    private Movie movie;
    private Theatre theatre;

    @BeforeEach
    void setup() {
        movie = new Movie();
        movie.setId(UUID.randomUUID());

        theatre = new Theatre();
        theatre.setId(UUID.randomUUID());
        theatre.setName("Test Theatre");

        show = new Show();
        show.setId(UUID.randomUUID());
        show.setMovie(movie);
        show.setTheatre(theatre);
        show.setShowDate(LocalDate.now());
        show.setStartTime(LocalTime.of(10, 0));

        showDTO = new ShowDTO(
                show.getId().toString(),
                movie.getId().toString(),
                "Movie Title",
                theatre.getId().toString(),
                theatre.getName(),
                show.getShowDate(),
                show.getStartTime(),
                LocalTime.NOON,
                100.0,
                "MORNING"
        );
    }

    // --------------------------------------------
    // saveShow()
    // --------------------------------------------
    @Test
    void saveShow_success() {
        when(showMapper.toEntity(showDTO)).thenReturn(show);
        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(theatreRepository.findById(any())).thenReturn(Optional.of(theatre));
        when(showRepository.findExistingShow(any(), any(), any(), any())).thenReturn(Optional.empty());
        when(showRepository.save(any())).thenReturn(show);
        when(showMapper.toDTO(show)).thenReturn(showDTO);

        ShowDTO result = service.saveShow(showDTO);

        assertThat(result).isNotNull();
        verify(seatService).assignShowToSeats(show);
    }

    @Test
    void saveShow_duplicateShow_throwsException() {
        when(showMapper.toEntity(showDTO)).thenReturn(show);
        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(theatreRepository.findById(any())).thenReturn(Optional.of(theatre));
        when(showRepository.findExistingShow(any(), any(), any(), any()))
                .thenReturn(Optional.of(show));

        assertThatThrownBy(() -> service.saveShow(showDTO))
                .isInstanceOf(DuplicateShowException.class);
    }

    // --------------------------------------------
    // patchShow()
    // --------------------------------------------
    @Test
    void patchShow_updatesFields() {
        Map<String, Object> updates = Map.of(
                "pricePerTicket", 180.0,
                "showType", "EVENING"
        );

        when(showRepository.findById(any())).thenReturn(Optional.of(show));
        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(theatreRepository.findById(any())).thenReturn(Optional.of(theatre));
        when(showRepository.save(any())).thenReturn(show);
        when(showMapper.toDTO(any())).thenReturn(showDTO);

        ShowDTO result = service.patchShow(showDTO, updates);

        assertThat(result).isNotNull();
        verify(showRepository).save(show);
    }

    @Test
    void patchShow_invalidField_throwsException() {
        Map<String, Object> updates = Map.of("invalid", "value");

        assertThatThrownBy(() -> service.patchShow(showDTO, updates))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --------------------------------------------
    // getShows()
    // --------------------------------------------
    @Test
    void getShows_success() {
        MovieDTO movieDTO = new MovieDTO(
                movie.getId(),
                "Some Movie",
                "Action",
                120,
                "English",
                LocalDate.now(),
                "UA"
        );
        Page<Show> page = new PageImpl<>(List.of(show));

        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(showRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(showMapper.toDTOList(any())).thenReturn(List.of(showDTO));

        PageResponse<ShowDTO> resp = service.getShows(movieDTO, "City", LocalDate.now(), 0, 10);

        assertThat(resp).isNotNull();
        assertThat(resp.getContent().size()).isEqualTo(1);
    }

    // --------------------------------------------
    // getShowById()
    // --------------------------------------------
    @Test
    void getShowById_success() {
        when(showRepository.findById(any())).thenReturn(Optional.of(show));
        when(showMapper.toDTO(show)).thenReturn(showDTO);

        Optional<ShowDTO> result = service.getShowById(show.getId().toString());

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(show.getId().toString());
    }

    @Test
    void getShowById_notFound() {
        when(showRepository.findById(any())).thenReturn(Optional.empty());

        Optional<ShowDTO> result = service.getShowById(show.getId().toString());

        assertThat(result).isEmpty();
    }

    // --------------------------------------------
    // deleteShow()
    // --------------------------------------------
    @Test
    void deleteShow_success() {
        service.deleteShow(show.getId().toString());

        verify(showRepository).deleteById(any());
    }

    // --------------------------------------------
    // getShowsByCityMovieAndDate()
    // --------------------------------------------
    @Test
    void getShowsByCityMovieAndDate_success() {

        // --- Setup 1st Show ---
        show.setStartTime(LocalTime.of(10, 0));
        show.setShowDate(LocalDate.now());
        show.setMovie(movie);
        show.setTheatre(theatre);

        // DTO for show 1
        ShowDTO dto1 = new ShowDTO(
                show.getId().toString(),
                movie.getId().toString(),
                "Movie Title",
                theatre.getId().toString(),
                theatre.getName(),
                show.getShowDate(),
                show.getStartTime(),
                null,
                100.0,
                "MORNING"
        );

        // --- Setup 2nd Show ---
        Show show2 = new Show();
        show2.setId(UUID.randomUUID());
        show2.setMovie(movie);
        show2.setTheatre(theatre);
        show2.setShowDate(LocalDate.now());
        show2.setStartTime(LocalTime.of(12, 0));

        ShowDTO dto2 = new ShowDTO(
                show2.getId().toString(),
                movie.getId().toString(),
                "Movie Title",
                theatre.getId().toString(),
                theatre.getName(),
                show2.getShowDate(),
                show2.getStartTime(),
                null,
                120.0,
                "AFTERNOON"
        );

        // Mock Mapper
        when(showMapper.toDTO(show)).thenReturn(dto1);
        when(showMapper.toDTO(show2)).thenReturn(dto2);

        // Mock Repo returning both shows
        when(showRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(show, show2));

        // Call Service
        List<ShowResponseDTO> result = service.getShowsByCityMovieAndDate(
                "City",
                movie.getId().toString(),
                LocalDate.now()
        );

        // Assertions
        assertThat(result).hasSize(1); // grouped by same theatre

        ShowResponseDTO responseDTO = result.get(0);

        // Check theatre info
        assertThat(responseDTO.theatreId()).isEqualTo(theatre.getId().toString());
        assertThat(responseDTO.theatreName()).isEqualTo(theatre.getName());

        // Check showDetails is a Map with 2 entries
        assertThat(responseDTO.showDetails()).hasSize(2);

        // Check keys contain both show IDs
        assertThat(responseDTO.showDetails().keySet())
                .containsExactly(show.getId().toString(), show2.getId().toString());
        // Sorted order: 10:00 then 12:00

        // Check individual DTO values
        assertThat(responseDTO.showDetails().get(show.getId().toString()))
                .isEqualTo(dto1);

        assertThat(responseDTO.showDetails().get(show2.getId().toString()))
                .isEqualTo(dto2);
    }
}
