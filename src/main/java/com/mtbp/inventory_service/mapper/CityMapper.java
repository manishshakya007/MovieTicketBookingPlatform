package com.mtbp.inventory_service.mapper;

import com.mtbp.inventory_service.dtos.CityDTO;
import com.mtbp.inventory_service.entities.City;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityMapper {
    CityDTO toDTO(City city);

    City toEntity(CityDTO dto);

    List<CityDTO> toDTOList(List<City> cities);
}
