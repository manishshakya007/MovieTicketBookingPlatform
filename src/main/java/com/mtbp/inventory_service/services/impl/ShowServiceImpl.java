package com.mtbp.inventory_service.services.impl;

import com.mtbp.inventory_service.dtos.MovieDTO;
import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.dtos.ShowDTO;
import com.mtbp.inventory_service.dtos.ShowResponseDTO;
import com.mtbp.inventory_service.entities.Movie;
import com.mtbp.inventory_service.entities.Show;
import com.mtbp.inventory_service.entities.Theatre;
import com.mtbp.inventory_service.exceptions.DuplicateShowException;
import com.mtbp.inventory_service.exceptions.ResourceNotFoundException;
import com.mtbp.inventory_service.mapper.ShowMapper;
import com.mtbp.inventory_service.repositories.MovieRepository;
import com.mtbp.inventory_service.repositories.ShowRepository;
import com.mtbp.inventory_service.repositories.TheatreRepository;
import com.mtbp.inventory_service.services.SeatService;
import com.mtbp.inventory_service.services.ShowService;
import com.mtbp.inventory_service.specifications.ShowSpecifications;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShowServiceImpl implements ShowService {
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ShowMapper showMapper;
    private final TheatreRepository theatreRepository;
    private final SeatService seatService;

    public ShowServiceImpl(ShowRepository showRepository, MovieRepository movieRepository, ShowMapper showMapper, TheatreRepository theatreRepository, SeatService seatService) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.showMapper = showMapper;
        this.theatreRepository = theatreRepository;
        this.seatService = seatService;
    }

    @Transactional
    public ShowDTO saveShow(ShowDTO showDTO) {
        Show savedShow = saveOrUpdateShowEntity(showDTO);
        seatService.assignShowToSeats(savedShow);
        return showMapper.toDTO(savedShow);
    }

    @Transactional
    public ShowDTO patchShow(ShowDTO existing, Map<String, Object> updates) {
        String id = existing.id();
        String movieId = existing.movieId();
        String movieTitle = existing.movieTitle();
        String theatreId = existing.theatreId();
        String theatreName = existing.theatreName();
        LocalDate showDate = existing.showDate();
        LocalTime startTime = existing.startTime();
        LocalTime endTime = existing.endTime();
        Double pricePerTicket = existing.pricePerTicket();
        String showType = existing.showType();

        for (var entry : updates.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "id" -> id = (String) value;
                case "movieId" -> movieId = (String) value;
                case "movieTitle" -> movieTitle = (String) value;
                case "theatreId" -> theatreId = (String) value;
                case "theatreName" -> theatreName = (String) value;
                case "showDate" -> showDate = LocalDate.parse((String) value);
                case "startTime" -> startTime = LocalTime.parse((String) value);
                case "endTime" -> endTime = LocalTime.parse((String) value);
                case "pricePerTicket" -> pricePerTicket = Double.valueOf(value.toString());
                case "showType" -> showType = (String) value;
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        }
        ShowDTO patched = new ShowDTO(
                id,
                movieId,
                movieTitle,
                theatreId,
                theatreName,
                showDate,
                startTime,
                endTime,
                pricePerTicket,
                showType
        );

        Show updatedShow = updateShowEntity(patched);

        return showMapper.toDTO(updatedShow);
    }

    private Show saveOrUpdateShowEntity(ShowDTO showDTO) {
        Show show = showMapper.toEntity(showDTO);

        if (showDTO.movieId() != null) {
            Movie movie = movieRepository.findById(UUID.fromString(showDTO.movieId()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Movie not found with id: " + showDTO.movieId()));
            show.setMovie(movie);
        }

        if (showDTO.theatreId() != null) {
            Theatre theatre = theatreRepository.findById(UUID.fromString(showDTO.theatreId()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Theatre not found with id: " + showDTO.theatreId()));
            show.setTheatre(theatre);
        }

        Optional<Show> existingShow =
                showRepository.findExistingShow(
                        show.getMovie().getId(),
                        show.getTheatre().getId(),
                        show.getShowDate(),
                        show.getStartTime()
                );
        if(existingShow.isPresent()){
            throw new DuplicateShowException("The Show you are trying to create is already exists in System");
        }

        // <-- Do NOT create again
        return existingShow.orElseGet(() -> showRepository.save(show));

    }

    private Show updateShowEntity(ShowDTO showDTO) {
        // Load existing show
        Show show = showRepository.findById(UUID.fromString(showDTO.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showDTO.id()));

        // Update fields (PATCH logic)
        if (showDTO.movieId() != null) {
            Movie movie = movieRepository.findById(UUID.fromString(showDTO.movieId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showDTO.movieId()));
            show.setMovie(movie);
        }

        if (showDTO.theatreId() != null) {
            Theatre theatre = theatreRepository.findById(UUID.fromString(showDTO.theatreId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: "
                            + showDTO.theatreId()));
            show.setTheatre(theatre);
        }

        if (showDTO.showDate() != null) show.setShowDate(showDTO.showDate());
        if (showDTO.startTime() != null) show.setStartTime(showDTO.startTime());
        if (showDTO.endTime() != null) show.setEndTime(showDTO.endTime());
        if (showDTO.pricePerTicket() != null) show.setPricePerTicket(showDTO.pricePerTicket());
        if (showDTO.showType() != null) show.setShowType(showDTO.showType());

        return showRepository.save(show);
    }

    public PageResponse<ShowDTO> getShows(MovieDTO movieDTO, String cityName, LocalDate date, int page, int size) {
        Movie movie = movieDTO != null ? movieRepository.findById(movieDTO.id()).orElse(null) : null;

        Specification<Show> spec = ShowSpecifications.byMovie(movie)
                .and(ShowSpecifications.byCity(cityName))
                .and(ShowSpecifications.byDate(date));

        Pageable pageable = PageRequest.of(page, size);
        Page<Show> showPage = showRepository.findAll(spec, pageable);

        List<ShowDTO> showDTOs = showMapper.toDTOList(showPage.getContent());

        return new PageResponse<>(
                showDTOs,
                showPage.getNumber(),
                showPage.getSize(),
                showPage.getTotalElements(),
                showPage.getTotalPages(),
                showPage.isLast()
        );
    }

    public Optional<ShowDTO> getShowById(String id) {
        return showRepository.findById(UUID.fromString(id))
                .map(showMapper::toDTO);
    }

    public void deleteShow(String showId) {
        showRepository.deleteById(UUID.fromString(showId));
    }

    public List<ShowResponseDTO> getShowsByCityMovieAndDate(String city, String movieIdStr, LocalDate date) {

        var spec = ShowSpecifications.byMovieId(UUID.fromString(movieIdStr))
                .and(ShowSpecifications.byCity(city))
                .and(ShowSpecifications.byDate(date));

        List<Show> shows = showRepository.findAll(spec);

        // Group By Theatre
        Map<UUID, List<Show>> grouped = shows.stream()
                .collect(Collectors.groupingBy(s -> s.getTheatre().getId()));

        List<ShowResponseDTO> response = new ArrayList<>();

        grouped.forEach((theatreId, showList) -> {

            Map<String, ShowDTO> showDetailsMap =
                    showList.stream()
                            .sorted(Comparator.comparing(Show::getStartTime))
                            .collect(Collectors.toMap(
                                    s -> s.getId().toString(),   // key = showId
                                    s -> showMapper.toDTO(s),     // value = ShowDTO
                                    (a, b) -> a,
                                    LinkedHashMap::new            // preserve sorted order
                            ));

            response.add(new ShowResponseDTO(
                    theatreId.toString(),
                    showList.getFirst().getTheatre().getName(),
                    showDetailsMap
            ));
        });

        return response;
    }
}
