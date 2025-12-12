package com.mtbp.inventory_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // ADMIN, CUSTOMER, THEATRE_OWNER

    @ManyToMany(mappedBy = "roles")
    private Set<Customer> users;

    public Role() {
    }
}
