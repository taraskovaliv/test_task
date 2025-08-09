package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.util.List;

@Entity
@DiscriminatorValue("MOTORCYCLE")
public class Motorcycle extends Vehicle {

    @Override
    public BigDecimal calculateParkingFee(int hoursParked) {
        return new BigDecimal("1.0").multiply(new BigDecimal(hoursParked));
    }

    @Transient
    @Override
    public List<SlotType> getAllowedSlotTypes() {
        return List.of(SlotType.MOTORCYCLE, SlotType.COMPACT, SlotType.LARGE);
    }
}
