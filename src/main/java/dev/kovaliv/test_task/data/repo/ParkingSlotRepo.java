package dev.kovaliv.test_task.data.repo;

import dev.kovaliv.test_task.data.entity.ParkingLevel;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.entity.ParkingSlot;
import dev.kovaliv.test_task.data.entity.SlotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepo extends JpaRepository<ParkingSlot, Long> {
    
    List<ParkingSlot> findAllByParkingLevel(ParkingLevel parkingLevel);
    
    List<ParkingSlot> findAllBySlotTypeAndOccupied(SlotType slotType, boolean occupied);
    
    long countByParkingLevelAndOccupied(ParkingLevel parkingLevel, boolean occupied);

    @Query("SELECT ps FROM ParkingSlot ps " +
            "WHERE ps.parkingLevel.parkingLot = ?1 AND ps.occupied = false AND ps.slotType IN ?2 " +
            "ORDER BY ps.slotType LIMIT 1")
    Optional<ParkingSlot> findAvailableSlot(ParkingLot parkingLot, List<SlotType> allowedSlotTypes);
}