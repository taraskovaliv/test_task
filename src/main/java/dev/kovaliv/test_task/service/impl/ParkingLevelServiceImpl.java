package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.AddParkingLevelDTO;
import dev.kovaliv.test_task.data.dto.ParkingLevelDTO;
import dev.kovaliv.test_task.data.entity.ParkingLevel;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.repo.ParkingLevelRepo;
import dev.kovaliv.test_task.data.repo.ParkingLotRepo;
import dev.kovaliv.test_task.service.ParkingLevelService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingLevelServiceImpl implements ParkingLevelService {

    private final ModelMapper modelMapper;
    private final ParkingLotRepo parkingLotRepo;
    private final ParkingLevelRepo parkingLevelRepo;

    @Override
    public ParkingLevelDTO createParkingLevel(AddParkingLevelDTO parkingLevelDTO) {
        if (parkingLevelDTO.getFloor() == null || parkingLevelDTO.getFloor() < 0) {
            throw new IllegalArgumentException("Parking level floor cannot be null or negative.");
        }
        if (parkingLevelRepo.existsByLotAndFloor(parkingLevelDTO.getParkingLotId(), parkingLevelDTO.getFloor())) {
            throw new IllegalArgumentException("Parking level with this floor already exists in the parking lot.");
        }
        Optional<ParkingLot> parkingLot = parkingLotRepo.findById(parkingLevelDTO.getParkingLotId());
        if (parkingLot.isEmpty()) {
            throw new IllegalArgumentException("Parking lot with id " + parkingLevelDTO.getParkingLotId() + " does not exist.");
        }
        ParkingLevel parkingLevel = dev.kovaliv.test_task.data.entity.ParkingLevel.builder()
                .floor(parkingLevelDTO.getFloor())
                .parkingLot(parkingLot.get())
                .build();
        ParkingLevel save = parkingLevelRepo.save(parkingLevel);
        return modelMapper.map(save, ParkingLevelDTO.class);
    }

    @Override
    public void deleteParkingLevel(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Parking level ID cannot be null.");
        }
        if (!parkingLevelRepo.existsById(id)) {
            throw new IllegalArgumentException("Parking level with id " + id + " does not exist.");
        }
        parkingLevelRepo.deleteById(id);
    }
}
