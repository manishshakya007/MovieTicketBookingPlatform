package com.mtbp.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "movies")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String title;

    private String genre;

    private Integer durationMinutes;

    private String language;

    private LocalDate releaseDate;

    private String rating;

    @OneToMany(mappedBy = "movie")
    @ToString.Exclude
    private List<Show> shows;

    public Movie() {
    }
}
