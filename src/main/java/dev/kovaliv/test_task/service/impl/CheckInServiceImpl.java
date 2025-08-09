package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.CheckInDto;
import dev.kovaliv.test_task.data.dto.CheckInResponseDTO;
import dev.kovaliv.test_task.data.dto.CheckOutDto;
import dev.kovaliv.test_task.data.dto.CheckOutResponseDTO;
import dev.kovaliv.test_task.data.entity.*;
import dev.kovaliv.test_task.data.repo.CheckInRepo;
import dev.kovaliv.test_task.data.repo.ParkingSlotRepo;
import dev.kovaliv.test_task.data.repo.VehicleRepo;
import dev.kovaliv.test_task.service.CheckInService;
import dev.kovaliv.test_task.service.ParkingLotService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static java.lang.System.currentTimeMillis;

@Log
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepo checkInRepo;
    private final VehicleRepo vehicleRepo;
    private final ParkingSlotRepo parkingSlotRepo;
    private final ParkingLotService parkingLotService;

    @Override
    public CheckInResponseDTO checkIn(Long lotId, CheckInDto checkInDto) {
        ParkingLot parkingLot = parkingLotService.getParkingLotById(lotId);
        if (checkInDto.getLicensePlate() == null || checkInDto.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty.");
        }
        VehicleType vehicleType = VehicleType.valueOf(checkInDto.getVehicleType().toUpperCase());
        Optional<Vehicle> availableVehicle = vehicleRepo.findByLicensePlate(checkInDto.getLicensePlate());
        availableVehicle.ifPresent(vehicle -> {
            if (checkInRepo.findActiveCheckIn(lotId, vehicle.getId()).isPresent()) {
                throw new IllegalArgumentException("Check in already exists.");
            }
        });
        Vehicle vehicle = availableVehicle.orElseGet(() ->
                switch (vehicleType) {
                    case CAR -> new Car();
                    case MOTORCYCLE -> new Motorcycle();
                    case TRUCK -> new Truck();
                }
        );
        vehicle.setLicensePlate(checkInDto.getLicensePlate());
        vehicle = vehicleRepo.save(vehicle);

        CheckIn checkIn = applyCheckIn(parkingLot, vehicle);
        log.info("CheckIn slot type: " + checkIn.getParkingSlot().getSlotType().name());
        return CheckInResponseDTO.builder()
                .vehicleType(vehicle.getVehicleType().name())
                .licensePlate(vehicle.getLicensePlate())
                .entryTime(checkIn.getCheckInDate())
                .slotId(checkIn.getParkingSlot().getId())
                .levelId(checkIn.getParkingSlot().getParkingLevel().getId())
                .levelFlour(checkIn.getParkingSlot().getParkingLevel().getFloor())
                .build();
    }

    @Transactional
    protected CheckIn applyCheckIn(ParkingLot parkingLot, Vehicle vehicle) {
        Optional<ParkingSlot> parkingSlot = parkingSlotRepo.findAvailableSlot(parkingLot, vehicle.getAllowedSlotTypes());
        if (parkingSlot.isEmpty()) {
            throw new IllegalStateException("No available parking slots for vehicle type: " + vehicle.getVehicleType());
        }
        ParkingSlot slot = parkingSlot.get();
        slot.setOccupied(true);
        parkingSlotRepo.save(slot);

        CheckIn checkIn = CheckIn.builder()
                .vehicle(vehicle)
                .parkingSlot(slot)
                .checkInDate(new Timestamp(currentTimeMillis()))
                .build();
        return checkInRepo.save(checkIn);
    }

    @Override
    public CheckOutResponseDTO checkOut(Long lotId, CheckOutDto checkOutDto) {
        ParkingLot parkingLot = parkingLotService.getParkingLotById(lotId);
        if (checkOutDto.getLicensePlate() == null || checkOutDto.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("Vehicle number cannot be null or empty.");
        }
        Vehicle vehicle = vehicleRepo.findByLicensePlate(checkOutDto.getLicensePlate())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle with license plate " + checkOutDto.getLicensePlate() + " not found."));

        CheckIn checkIn = applyCheckOut(checkOutDto, vehicle, parkingLot);
        return CheckOutResponseDTO.builder()
                .checkInDate(checkIn.getCheckInDate())
                .checkOutDate(checkIn.getCheckOutDate())
                .fee(checkIn.getFee())
                .build();
    }

    @Transactional
    protected CheckIn applyCheckOut(CheckOutDto checkOutDto, Vehicle vehicle, ParkingLot parkingLot) {
        CheckIn checkIn = checkInRepo.findActiveCheckIn(parkingLot.getId(), vehicle.getId())
                .orElseThrow(() -> new IllegalArgumentException("No active check-in found for vehicle with license plate " + checkOutDto.getLicensePlate() + "."));
        checkIn.setCheckOutDate(new Timestamp(currentTimeMillis()));
        checkIn.setFee(vehicle.calculateParkingFee(checkIn.getCheckInDate(), checkIn.getCheckOutDate()));
        checkIn = checkInRepo.save(checkIn);
        ParkingSlot parkingSlot = checkIn.getParkingSlot();
        parkingSlot.setOccupied(false);
        parkingSlotRepo.save(parkingSlot);
        return checkIn;
    }

    @Override
    public List<CheckInResponseDTO> getActiveCheckIns(Long lotId) {
        ParkingLot parkingLot = parkingLotService.getParkingLotById(lotId);
        List<CheckIn> activeCheckIns = checkInRepo.findActiveCheckIns(parkingLot.getId());
        return activeCheckIns.stream()
                .map(checkIn -> CheckInResponseDTO.builder()
                        .vehicleType(checkIn.getVehicle().getVehicleType().name())
                        .licensePlate(checkIn.getVehicle().getLicensePlate())
                        .entryTime(checkIn.getCheckInDate())
                        .slotId(checkIn.getParkingSlot().getId())
                        .levelId(checkIn.getParkingSlot().getParkingLevel().getId())
                        .levelFlour(checkIn.getParkingSlot().getParkingLevel().getFloor())
                        .build())
                .toList();
    }
}
