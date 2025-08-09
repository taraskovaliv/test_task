package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.CreateParkingLotDTO;
import dev.kovaliv.test_task.data.dto.ParkingLotDTO;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.repo.ParkingLotRepo;
import dev.kovaliv.test_task.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ModelMapper modelMapper;
    private final ParkingLotRepo parkingLotRepo;

    @Override
    public ParkingLotDTO createParkingLot(CreateParkingLotDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Parking lot name cannot be null or empty.");
        }
        if (parkingLotRepo.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Parking lot with this name already exists.");
        }
        ParkingLot parkingLot = ParkingLot.builder().name(dto.getName()).build();
        parkingLot = parkingLotRepo.save(parkingLot);
        return modelMapper.map(parkingLot, ParkingLotDTO.class);
    }

    @Override
    public void deleteParkingLot(Long id) {
        if (!parkingLotRepo.existsById(id)) {
            throw new IllegalArgumentException("Parking lot with id " + id + " does not exist.");
        }
        parkingLotRepo.deleteById(id);
    }

    @Override
    public ParkingLot getParkingLotById(Long id) {
        return parkingLotRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot with id " + id + " does not exist."));
    }
}
