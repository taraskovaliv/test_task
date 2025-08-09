package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.List;

@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {

    @Override
    public BigDecimal calculateParkingFee(int hoursParked) {
        return new BigDecimal("3.0").multiply(new BigDecimal(hoursParked));
    }

    @Transient
    @Override
    public List<SlotType> getAllowedSlotTypes() {
        return List.of(SlotType.LARGE, SlotType.HANDICAPPED);
    }
}
