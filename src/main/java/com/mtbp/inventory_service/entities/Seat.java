package com.mtbp.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Seat {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String seatNumber; // Example L1, M1, N1

    private String seatType; // REGULAR, PREMIUM, VIP

    private Boolean available = true;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    @ToString.Exclude
    private Theatre theatre;

    @ManyToOne
    @JoinColumn(name = "show_id")
    @ToString.Exclude
    private Show show;

    public Seat() {
    }
}
