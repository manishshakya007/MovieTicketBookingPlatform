package com.mtbp.inventory_service.services.impl;

import com.mtbp.inventory_service.dtos.MovieDTO;
import com.mtbp.inventory_service.dtos.PageResponse;
import com.mtbp.inventory_service.entities.Movie;
import com.mtbp.inventory_service.exceptions.BadRequestException;
import com.mtbp.inventory_service.mapper.MovieMapper;
import com.mtbp.inventory_service.repositories.MovieRepository;
import com.mtbp.inventory_service.services.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public MovieDTO saveMovie(MovieDTO movieDTO) {
        Movie movieEntity = movieMapper.toEntity(movieDTO);

        Movie savedMovie = movieRepository.save(movieEntity);

        return movieMapper.toDTO(savedMovie);
    }

    public MovieDTO patchMovie(MovieDTO movieDTO, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            try {
                Field field = MovieDTO.class.getDeclaredField(key);
                field.setAccessible(true);

                if (field.getType().equals(LocalDate.class) && value instanceof String) {
                    field.set(movieDTO, LocalDate.parse((String) value));
                } else if (field.getType().equals(Integer.class) && value instanceof Number) {
                    field.set(movieDTO, ((Number) value).intValue());
                } else {
                    field.set(movieDTO, value);
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("Invalid Movie field: {}", key);
                throw new BadRequestException("Invalid Movie field: " + key);
            }
        });

        return saveMovie(movieDTO);
    }

    public PageResponse<MovieDTO> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<MovieDTO> movieDTOs = movieMapper.toDTOList(moviePage.getContent());

        return new PageResponse<>(
                movieDTOs,
                moviePage.getNumber(),
                moviePage.getSize(),
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast()
        );
    }

    public Optional<MovieDTO> getMovieById(String id) {
        return movieRepository.findById(UUID.fromString(id))
                .map(movieMapper::toDTO);
    }

    public Optional<MovieDTO> getMovieByTitle(String title) {
        return movieRepository.findByTitle(title)
                .map(movieMapper::toDTO);
    }
}
