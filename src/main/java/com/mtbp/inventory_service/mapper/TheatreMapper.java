package com.mtbp.inventory_service.mapper;


import com.mtbp.inventory_service.dtos.TheatreDTO;
import com.mtbp.inventory_service.entities.Theatre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TheatreMapper {
    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "city.name", target = "cityName")
    TheatreDTO toDTO(Theatre theatre);

    Theatre toEntity(TheatreDTO dto);

    List<TheatreDTO> toDTOList(List<Theatre> theatres);
}
