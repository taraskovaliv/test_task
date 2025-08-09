package dev.kovaliv.test_task.service;

import dev.kovaliv.test_task.data.dto.CreateParkingLotDTO;
import dev.kovaliv.test_task.data.dto.ParkingLotDTO;
import dev.kovaliv.test_task.data.entity.ParkingLot;

public interface ParkingLotService {

    ParkingLotDTO createParkingLot(CreateParkingLotDTO dto);

    void deleteParkingLot(Long id);

    ParkingLot getParkingLotById(Long id);
}
