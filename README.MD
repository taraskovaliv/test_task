# Parking Lot Management System

A Spring Boot REST API for managing multi-level parking lots with different vehicle types and dynamic pricing.

## Design Rationale

### Architecture Overview
This system follows a layered architecture pattern with clear separation of concerns:

- **Controller Layer**: REST endpoints for parking operations
- **Service Layer**: Business logic for parking management
- **Repository Layer**: JPA-based data persistence
- **Entity Layer**: Domain models with inheritance hierarchy

### Key Design Decisions

#### 1. Vehicle Type Hierarchy
Uses JPA single-table inheritance (`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`) to model different vehicle types:
- **Vehicle** (abstract base class)
- **Car**, **Motorcycle**, **Truck** (concrete implementations)

Each vehicle type implements its own pricing logic through the abstract `calculateParkingFee()` method.

#### 2. Slot Type System
Implements an enum-based slot typing system (`SlotType`) that defines which vehicles can park in which slots:
- Enforces business rules (e.g., motorcycles can park in any slot, trucks need special slots)
- Supports extensibility for new slot types

#### 3. Hierarchical Parking Structure
Models real-world parking facilities with three levels:
- **ParkingLot**: Top-level container
- **ParkingLevel**: Individual floors/levels
- **ParkingSlot**: Individual parking spaces

#### 4. Time-Based Pricing
Implements flexible pricing calculation:
- Hourly billing with partial hour rounding up
- Vehicle-specific rates
- Easy extensibility for complex pricing models

### Technology Stack
- **Spring Boot 3.5.4**: Modern framework with Java 21 support
- **Spring Data JPA**: ORM and repository pattern
- **H2 Database**: In-memory database for development/testing
- **ModelMapper**: DTO mapping
- **Lombok**: Boilerplate reduction

## Installation & Setup

### Prerequisites
- Java 21 or higher
- Gradle 7.0+ (or use included wrapper)

### Quick Start
1. Clone the repository
2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
3. Application starts on `http://localhost:8080`
4. H2 Console available at `http://localhost:8080/h2-console`

### Database Configuration
Uses H2 in-memory database by default. Configuration in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:test_db
spring.datasource.username=sa
spring.datasource.password=
```

## API Usage Examples

### 1. Create a Parking Lot
```bash
POST /parking-lots
Content-Type: application/json

{
  "name": "Downtown Parking",
  "address": "123 Main St"
}
```

### 2. Add Parking Level
```bash
POST /parking-levels
Content-Type: application/json

{
  "parkingLotId": 1,
  "levelNumber": 1,
  "name": "Ground Floor"
}
```

### 3. Add Parking Slots
```bash
POST /parking-slots
Content-Type: application/json

{
  "parkingLevelId": 1,
  "slotNumber": "A01",
  "slotType": "REGULAR"
}
```

### 4. Check In Vehicle
```bash
POST /check-in/1
Content-Type: application/json

{
  "licensePlate": "ABC123",
  "vehicleType": "CAR"
}
```

### 5. Check Out Vehicle
```bash
POST /check-out/1
Content-Type: application/json

{
  "licensePlate": "ABC123"
}
```

### 6. View Active Check-ins
```bash
GET /active-check-ins/1
```

## Testing

Run tests using Gradle:
```bash
./gradlew test
```

Tests include:
- Unit tests for services
- Controller integration tests
- Repository layer tests

## Slot Types

- **REGULAR**: Standard parking spaces (cars, motorcycles)
- **COMPACT**: Smaller spaces (motorcycles, small cars)
- **LARGE**: Oversized spaces (trucks, large vehicles)

## Vehicle Types & Pricing

- **MOTORCYCLE**: $1/hour, can use any slot type
- **CAR**: $2/hour, uses regular and compact slots
- **TRUCK**: $3/hour, requires large slots only

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/parking-lots` | Create parking lot |
| GET | `/parking-lots` | List parking lots |
| POST | `/parking-levels` | Add parking level |
| GET | `/parking-levels/{lotId}` | Get levels for lot |
| POST | `/parking-slots` | Add parking slot |
| GET | `/parking-slots/{levelId}` | Get slots for level |
| POST | `/check-in/{lotId}` | Check in vehicle |
| POST | `/check-out/{lotId}` | Check out vehicle |
| GET | `/active-check-ins/{lotId}` | View active check-ins |