package com.mtbp.inventory_service.repositories;

import com.mtbp.inventory_service.entities.Seat;
import com.mtbp.inventory_service.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID>, JpaSpecificationExecutor<Seat> {
    List<Seat> findByShowAndAvailableTrue(Show show);

    List<Seat> findByShow(Show show);

    List<Seat> findByTheatreId(UUID theatreId);

    List<Seat> findByTheatreIdAndId(UUID theatreId, UUID seatId);

    Optional<Seat> findById(UUID seatId);

    Optional<Seat> findByIdAndTheatreId(UUID seatId, UUID theatreId);

    Optional<Seat> findByTheatreIdAndSeatNumber(UUID theatreId, String seatNumber);

    List<Seat> findBySeatNumber(String seatNumber);

    @Modifying
    @Query("UPDATE Seat s SET s.show.id = :showId WHERE s.theatre.id = :theatreId")
    int updateShowIdByTheatreId(@Param("theatreId") UUID theatreId, @Param("showId") UUID showId);

    List<Seat> findByAvailableTrue();
}
