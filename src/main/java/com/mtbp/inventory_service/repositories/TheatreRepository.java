package com.mtbp.inventory_service.repositories;

import com.mtbp.inventory_service.entities.City;
import com.mtbp.inventory_service.entities.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, UUID> {
    List<Theatre> findByCity(City city);

    List<Theatre> findByCityName(String cityName);
}
