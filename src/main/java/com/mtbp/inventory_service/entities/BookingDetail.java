package com.mtbp.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingDetail {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @ToString.Exclude
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    @ToString.Exclude
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "show_id")
    @ToString.Exclude
    private Show show;

    private Double price;

    private Double discountApplied;

    public BookingDetail() {
    }
}
