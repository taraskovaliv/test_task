package dev.kovaliv.test_task.data.repo;

import dev.kovaliv.test_task.data.entity.CheckIn;
import dev.kovaliv.test_task.data.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepo extends JpaRepository<CheckIn, Long> {

    @Query("SELECT c FROM CheckIn c " +
            "WHERE c.parkingSlot.parkingLevel.parkingLot.id = ?1 " +
            "AND c.vehicle.id = ?2 " +
            "AND c.checkOutDate IS NULL " +
            "ORDER BY c.checkInDate DESC LIMIT 1")
    Optional<CheckIn> findActiveCheckIn(Long parkingLotId, Long vehicleId);

    @Query("SELECT c FROM CheckIn c " +
            "WHERE c.parkingSlot.parkingLevel.parkingLot.id = ?1 " +
            "AND c.checkOutDate IS NULL")
    List<CheckIn> findActiveCheckIns(Long parkingLotId);
}
