package dev.kovaliv.test_task.service;

import dev.kovaliv.test_task.data.dto.AddParkingLevelDTO;
import dev.kovaliv.test_task.data.dto.ParkingLevelDTO;

public interface ParkingLevelService {

    ParkingLevelDTO createParkingLevel(AddParkingLevelDTO parkingLevelDTO);

    void deleteParkingLevel(Long id);
}
