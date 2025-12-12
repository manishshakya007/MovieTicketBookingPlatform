package com.mtbp.inventory_service.controllers;

import com.mtbp.inventory_service.dtos.*;
import com.mtbp.inventory_service.exceptions.ResourceNotFoundException;
import com.mtbp.inventory_service.services.MovieService;
import com.mtbp.inventory_service.services.ShowService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {
    private final ShowService showService;
    private final MovieService movieService;

    public ShowController(ShowService showService, MovieService movieService) {
        this.showService = showService;
        this.movieService = movieService;
    }

    @PostMapping("/create")
    public ResponseEntity<ShowDTO> createShow(@RequestBody ShowDTO showDTO) {
        ShowDTO savedShow = showService.saveShow(showDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShow);
    }

    @GetMapping("/browse")
    public ResponseEntity<PageResponse<ShowDTO>> browseShows(
            @RequestParam(required = false) String movieTitle,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        MovieDTO movieDto = null;
        if (movieTitle != null) {
            movieDto = movieService.getMovieByTitle(movieTitle)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with title: " + movieTitle));
        }

        PageResponse<ShowDTO> showsDto = showService.getShows(movieDto, cityName, date, page, size);
        return ResponseEntity.ok(showsDto);
    }

    @PatchMapping("/{showId}")
    public ResponseEntity<ShowDTO> patchShow(@PathVariable("showId") String showId, @RequestBody Map<String, Object> updates) {
        ShowDTO existingShow = showService.getShowById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        ShowDTO updatedShow = showService.patchShow(existingShow, updates);
        return ResponseEntity.ok(updatedShow);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowById(@PathVariable String id) {
        Optional<ShowDTO> optionalShow = showService.getShowById(id);

        return optionalShow
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{showId}")
    public ResponseEntity<Void> deleteShow(@PathVariable("showId") String showId) {
        showService.deleteShow(showId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/city/search")
    public List<ShowResponseDTO> searchShows(
            @Valid @ModelAttribute ShowSearchRequest request) {
        return showService.getShowsByCityMovieAndDate(
                request.city(),
                request.movieId(),
                request.date()
        );
    }
}
