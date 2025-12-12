package com.mtbp.inventory_service.repositories;

import com.mtbp.inventory_service.entities.Booking;
import com.mtbp.inventory_service.entities.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, UUID> {
    List<BookingDetail> findByBooking(Booking booking);
}
