package com.mtbp.inventory_service.components;

import com.mtbp.inventory_service.entities.*;
import com.mtbp.inventory_service.repositories.*;
import com.mtbp.inventory_service.services.TheatreService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DependsOn({"roleInitialization"})
@Component
@RequiredArgsConstructor
public class Initialization {
    private final TheatreRepository theatreRepository;
    private final MovieRepository movieRepository;
    private final CityRepository cityRepository;
    private final SeatRepository seatRepository;
    private final TheatreService theatreService;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final ShowRepository showRepository;

    @PostConstruct
    public void init() {
        List<City> cities = createCities();
        List<Theatre> theatres = createTheatres(cities);
        createSeatsForTheatres(theatres);
        List<Movie> movies = createMovies();
        createShows(movies, theatres);
        createCustomers();
    }

    private List<City> createCities() {
        City noida = new City(null, "Noida", null);
        City gurgaon = new City(null, "Gurgram", null);
        City mumbai = new City(null, "Mumbai", null);

        List<City> cities = List.of(noida, gurgaon, mumbai);
        return cityRepository.saveAll(cities);
    }

    private List<Theatre> createTheatres(List<City> cities) {
        City noida = cities.stream().filter(c -> c.getName().equals("Noida")).findFirst().orElseThrow();
        City gurugram = cities.stream().filter(c -> c.getName().equals("Gurgram")).findFirst().orElseThrow();
        City mumbai = cities.stream().filter(c -> c.getName().equals("Mumbai")).findFirst().orElseThrow();

        List<Theatre> theatres = new ArrayList<>();

        theatres.add(new Theatre(null, "PVR Superplex Noida", "Fourth Floor, DLF Mall of India, Sector 18, Noida", 10, noida, null, null));
        theatres.add(new Theatre(null, "Cineplex Mumbai Central", "123 Central Street, Mumbai", 10, mumbai, null, null));
        theatres.add(new Theatre(null, "IMAX Mumbai", "456 Marine Drive, Mumbai", 10, mumbai, null, null));
        theatres.add(new Theatre(null, "PVR City Center Gurgaon", "DLF City Center Mall, Sector 28, Gurugram", 10, gurugram, null, null));

        return theatreRepository.saveAll(theatres);
    }

    private void createSeatsForTheatres(List<Theatre> theatres) {
        for (Theatre theatre : theatres) {
            int totalSeats = theatre.getTotalSeats();
            List<Seat> allSeats = theatreService.prepareSeatsForTheatre(theatre, totalSeats);
            seatRepository.saveAll(allSeats);
        }
    }

    private List<Movie> createMovies() {
        List<Movie> movies = List.of(
                new Movie(null, "De De Pyar De 2", "Family-Comedy", 150, "Hindi",
                        LocalDate.of(2025, 9, 26), "PG-13", null),
                new Movie(null, "Avengers: Endgame", "Action", 181, "English",
                        LocalDate.of(2019, 4, 26), "PG-13", null),
                new Movie(null, "Inception", "Sci-Fi", 148, "English",
                        LocalDate.of(2010, 7, 16), "PG-13", null),
                new Movie(null, "Interstellar", "Sci-Fi", 169, "English",
                        LocalDate.of(2014, 11, 7), "PG-13", null),
                new Movie(null, "3 Idiots", "Drama", 170, "Hindi",
                        LocalDate.of(2009, 12, 25), "PG", null),
                new Movie(null, "Avatar", "Sci-Fi", 162, "English",
                        LocalDate.of(2009, 12, 18), "PG-13", null)
        );

        movieRepository.saveAll(movies);
        return movies;
    }

    private void createCustomers() {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseThrow();
        Role theatreOwnerRole = roleRepository.findByName("THEATRE_OWNER").orElseThrow();

        List<Customer> customers = List.of(
                new Customer(null, "Gokhu San", "gokhu.san@dragonballz.com", "9876543210", null, Set.of(adminRole)),
                new Customer(null, "Naruto Uzimaki", "naruto.uzimaki@ninzawar.com", "9123456780", null, Set.of(customerRole)),
                new Customer(null, "Luffy D Monkey", "luffy.d.monkey@onepiece.com", "9988776655", null, Set.of(theatreOwnerRole))
        );

        customerRepository.saveAll(customers);
    }

    private void createShows(List<Movie> movies, List<Theatre> theatres) {

        // Fetch movie objects by name (for easy matching)
        Movie inception = movies.stream().filter(m -> m.getTitle().equals("Inception")).findFirst().orElseThrow();
        Movie interstellar = movies.stream().filter(m -> m.getTitle().equals("Interstellar")).findFirst().orElseThrow();
        Movie avengers = movies.stream().filter(m -> m.getTitle().equals("Avengers: Endgame")).findFirst().orElseThrow();

        // Fetch theatres by name
        Theatre pvrNoida = theatres.stream().filter(t -> t.getName().contains("Noida")).findFirst().orElseThrow();
        Theatre cineplexMumbai = theatres.stream().filter(t -> t.getName().contains("Cineplex Mumbai")).findFirst().orElseThrow();
        Theatre imaxMumbai = theatres.stream().filter(t -> t.getName().contains("IMAX Mumbai")).findFirst().orElseThrow();

        List<Show> shows = List.of(
                // ----------------- NOIDA SHOWS -----------------
                new Show(null, inception, pvrNoida,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 30),
                        350.0, "morning",
                        null, null),

                new Show(null, inception, pvrNoida,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(14, 0),
                        LocalTime.of(16, 30),
                        450.0, "afternoon",
                        null, null),

                new Show(null, avengers, pvrNoida,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(18, 0),
                        LocalTime.of(21, 0),
                        600.0, "evening",
                        null, null),

                // ----------------- MUMBAI SHOWS -----------------
                new Show(null, interstellar, cineplexMumbai,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(11, 0),
                        LocalTime.of(13, 45),
                        500.0, "morning",
                        null, null),

                new Show(null, interstellar, imaxMumbai,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(15, 0),
                        LocalTime.of(18, 0),
                        800.0, "evening",
                        null, null),

                new Show(null, avengers, imaxMumbai,
                        LocalDate.of(2025, 11, 29),
                        LocalTime.of(20, 0),
                        LocalTime.of(23, 0),
                        900.0, "night",
                        null, null)
        );

        showRepository.saveAll(shows);
    }
}
