package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.AUTO;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 20, columnDefinition = "VARCHAR(20)", unique = true)
    private String licensePlate;

    @OneToMany
    private Set<CheckIn> checkIns;

    public abstract BigDecimal calculateParkingFee(int hoursParked);

    @Transient
    public abstract List<SlotType> getAllowedSlotTypes();

    public BigDecimal calculateParkingFee(Timestamp startTime, Timestamp endTime) {
        int hoursParked = (int) (Duration.between(startTime.toLocalDateTime(), endTime.toLocalDateTime()).toSeconds() + 3599) / 3600;
        return calculateParkingFee(hoursParked);
    }

    public VehicleType getVehicleType() {
        return switch (this) {
            case Car ignored -> VehicleType.CAR;
            case Motorcycle ignored -> VehicleType.MOTORCYCLE;
            case Truck ignored -> VehicleType.TRUCK;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}
