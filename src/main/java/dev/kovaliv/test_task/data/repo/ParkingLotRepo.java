package dev.kovaliv.test_task.data.repo;

import dev.kovaliv.test_task.data.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepo extends JpaRepository<ParkingLot, Long> {

    boolean existsByName(String name);
}
