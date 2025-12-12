package com.mtbp.inventory_service.mapper;

import com.mtbp.inventory_service.dtos.MovieDTO;
import com.mtbp.inventory_service.entities.Movie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieDTO toDTO(Movie movie);

    Movie toEntity(MovieDTO dto);

    List<MovieDTO> toDTOList(List<Movie> movies);
}
