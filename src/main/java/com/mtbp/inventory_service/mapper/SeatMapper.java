package com.mtbp.inventory_service.mapper;

import com.mtbp.inventory_service.dtos.SeatDTO;
import com.mtbp.inventory_service.entities.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "theatre.id", target = "theatreId")
    @Mapping(source = "theatre.name", target = "theatreName")
    @Mapping(source = "show.id", target = "showId")
    SeatDTO toDTO(Seat seat);

    @Mapping(source = "theatreId", target = "theatre.id")
    @Mapping(source = "showId", target = "show.id")
    @Mapping(target = "theatre", ignore = true)
    @Mapping(target = "show", ignore = true)
    Seat toEntity(SeatDTO dto);

    List<SeatDTO> toDTOList(List<Seat> seats);
}
