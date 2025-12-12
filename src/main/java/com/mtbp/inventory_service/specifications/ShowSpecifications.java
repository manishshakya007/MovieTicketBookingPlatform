package com.mtbp.inventory_service.specifications;

import com.mtbp.inventory_service.entities.Movie;
import com.mtbp.inventory_service.entities.Show;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class ShowSpecifications {
    public static Specification<Show> byMovie(Movie movie) {
        return (root, query, criteriaBuilder) -> {
            if (movie == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("movie"), movie);
        };
    }

    public static Specification<Show> byCity(String cityName) {
        return (root, query, criteriaBuilder) -> {
            if (cityName == null || cityName.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("theatre").get("city").get("name"), cityName);
        };
    }

    public static Specification<Show> byDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("showDate"), date);
        };
    }

    public static Specification<Show> byMovieId(UUID movieId) {
        return (root, query, cb) -> {
            if (movieId == null) return cb.conjunction();
            return cb.equal(root.get("movie").get("id"), movieId);
        };
    }
}
