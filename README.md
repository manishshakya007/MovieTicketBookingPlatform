Movie Ticket Booking – Inventory Service
(Spring Boot • JPA • H2 • Microservices Architecture)

This service manages Theatres, Seats, Shows, and Movie Inventory for a movie-ticket booking platform (similar to BookMyShow).
It exposes APIs to:

Create theatres & seats

Create shows

Assign seats to shows

Fetch available seats

Retrieve show details

Manage inventory for bookings

🚀 Features

Manage theatres & seat layouts

Create shows without duplicates

Auto-assign seats to shows

Prevent overlapping or duplicate show creation

Clean JPA relationships between Show, Seat, Movie, Theatre

H2

Optional integration with microservices (Booking Service, Payment Service)

🏗 Architecture Overview
┌──────────────────────────┐
│   API Gateway (Optional) │
└───────────────┬──────────┘
▼
┌────────────────┐
│ Inventory API  │
│ (This Service) │
└───────┬────────┘
▼
┌───────────────────────┐
│ Spring Boot + JPA ORM │
└─────────────┬─────────┘
▼
┌───────────────────────────┐
│ DB: H2                    │
└───────────────────────────┘

📦 Technologies Used
Component	Technology
Backend	Spring Boot 3, Java 21
ORM	Spring Data JPA, Hibernate
Database	H2 (dev), PostgreSQL (prod)
Build	Maven
Containerization (optional)	Docker
Logging	SLF4J / Logback
🗃 Database Entities
Seat

ID (UUID)

Seat Number (A1, B2)

Seat Type (REGULAR, PREMIUM, VIP)

Availability

Theatre (Many-to-One)

Show (Many-to-One)

Show

Movie

Theatre

Show Date & Time

Assigned Seats (One-to-Many)

Price per Ticket

Booking Details

Theatre

Name

Address

Total Seats

City

Shows

Seats

Movie

Title

Genre

Release Date

Duration

Language

Rating

🔐 Reading DB Credentials from Environment Variables

Spring Boot supports dynamic DB configs:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}


Set environment variables:

Linux / macOS:

export DB_URL=db url
export DB_USERNAME=username
export DB_PASSWORD=password


Windows PowerShell:

setx DB_URL "db url"
setx DB_USERNAME "username"
setx DB_PASSWORD "password"

🛠 Setup Instructions
✅ 1. Clone Repository
git clone <repo url>
cd inventory-service

✅ 2. Run Application (H2 Default)
mvn spring-boot:run


App runs at:

http://localhost:8080

✅ 3. Access H2 Console

Enable in application.properties:

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console


Open browser:

http://localhost:8080/h2-console


Use:

JDBC URL: db url
Username: username
Password: password