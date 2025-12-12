package com.mtbp.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shows")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Show {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @ToString.Exclude
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    @ToString.Exclude
    private Theatre theatre;

    private LocalDate showDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Double pricePerTicket;

    private String showType; // morning, afternoon, evening

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Seat> seats;

    @OneToMany(mappedBy = "show")
    @ToString.Exclude
    private List<BookingDetail> bookingDetails;

    public Show() {
    }
}
