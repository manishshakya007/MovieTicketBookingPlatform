package com.mtbp.inventory_service.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "theatres")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;

    private Integer totalSeats;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonBackReference
    @ToString.Exclude
    private City city;

    @OneToMany(mappedBy = "theatre")
    @ToString.Exclude
    private List<Show> shows;

    @OneToMany(mappedBy = "theatre")
    @ToString.Exclude
    private List<Seat> seats;

    public Theatre() {
    }
}
