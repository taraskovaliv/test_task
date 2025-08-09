package dev.kovaliv.test_task.data.repo;

import dev.kovaliv.test_task.data.entity.ParkingLevel;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingLevelRepo extends JpaRepository<ParkingLevel, Long> {

    @Query("SELECT COUNT(pl) > 0 FROM ParkingLevel pl WHERE pl.parkingLot.id = ?1 AND pl.floor = ?2")
    boolean existsByLotAndFloor(Long lotId, Integer floor);
    
    List<ParkingLevel> findAllByParkingLot(ParkingLot parkingLot);
}
