package com.mtbp.inventory_service.repositories;

import com.mtbp.inventory_service.entities.City;
import com.mtbp.inventory_service.entities.Movie;
import com.mtbp.inventory_service.entities.Show;
import com.mtbp.inventory_service.entities.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShowRepository extends JpaRepository<Show, UUID>, JpaSpecificationExecutor<Show> {
    List<Show> findByMovieAndTheatreInAndShowDate(Movie movie, List<Theatre> theatres, LocalDate showDate);

    List<Show> findByTheatreAndShowDate(Theatre theatre, LocalDate showDate);

    List<Show> findByShowDateAndStartTimeBetween(LocalDate showDate, LocalTime start, LocalTime end);

    List<Show> findByMovieIdAndTheatre_CityAndShowDate(UUID movieId, City city, LocalDate showDate);

    @Query("""
    SELECT s FROM Show s 
    WHERE s.movie.id = :movieId 
      AND s.theatre.id = :theatreId
      AND s.showDate = :showDate
      AND s.startTime = :startTime
    """)
    Optional<Show> findExistingShow(
            UUID movieId,
            UUID theatreId,
            LocalDate showDate,
            LocalTime startTime
    );
}
