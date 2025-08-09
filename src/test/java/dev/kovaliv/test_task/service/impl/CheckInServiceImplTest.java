package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.CheckInDto;
import dev.kovaliv.test_task.data.dto.CheckInResponseDTO;
import dev.kovaliv.test_task.data.dto.CheckOutDto;
import dev.kovaliv.test_task.data.dto.CheckOutResponseDTO;
import dev.kovaliv.test_task.data.entity.*;
import dev.kovaliv.test_task.data.repo.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CheckInServiceImplTest {

    @Autowired
    private CheckInServiceImpl checkInService;

    @Autowired
    private CheckInRepo checkInRepo;

    @Autowired
    private VehicleRepo vehicleRepo;

    @Autowired
    private ParkingSlotRepo parkingSlotRepo;

    @Autowired
    private ParkingLevelRepo parkingLevelRepo;

    @Autowired
    private ParkingLotRepo parkingLotRepo;

    private ParkingLot testParkingLot;

    private ParkingLevel testParkingLevel;

    @BeforeEach
    void setUp() {
        checkInRepo.deleteAll();
        vehicleRepo.deleteAll();
        parkingSlotRepo.deleteAll();
        parkingLevelRepo.deleteAll();
        parkingLotRepo.deleteAll();

        parkingSlotRepo.deleteAll();
        parkingLevelRepo.deleteAll();
        parkingLotRepo.deleteAll();

        testParkingLot = ParkingLot.builder()
                .name("Test Lot")
                .levels(new HashSet<>())
                .build();
        testParkingLot = parkingLotRepo.save(testParkingLot);

        testParkingLevel = ParkingLevel.builder()
                .floor(1)
                .parkingLot(testParkingLot)
                .slots(new HashSet<>())
                .build();
        testParkingLevel = parkingLevelRepo.save(testParkingLevel);

        parkingSlotRepo.saveAll(List.of(
                ParkingSlot.builder().slotType(SlotType.MOTORCYCLE).parkingLevel(testParkingLevel).build(),
                ParkingSlot.builder().slotType(SlotType.COMPACT).parkingLevel(testParkingLevel).build(),
                ParkingSlot.builder().slotType(SlotType.LARGE).parkingLevel(testParkingLevel).build()
        ));
    }

    @Test
    void shouldCheckInVehicleSuccessfully() {
        String licencePlate = "ABC123";
        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate(licencePlate)
                .vehicleType(VehicleType.CAR.name())
                .build();
        CheckInResponseDTO response = checkInService.checkIn(testParkingLot.getId(), checkInDto);

        assertNotNull(response);
        assertNotNull(response.getEntryTime());
        assertNotNull(response.getSlotId());
        assertEquals(licencePlate, response.getLicensePlate());
        assertEquals(VehicleType.CAR.name(), response.getVehicleType());
        assertEquals(testParkingLevel.getId(), response.getLevelId());
        assertEquals(testParkingLevel.getFloor(), response.getLevelFlour());

        Optional<ParkingSlot> parkingSlot = parkingSlotRepo.findById(response.getSlotId());

        assertTrue(parkingSlot.isPresent());
        assertTrue(parkingSlot.get().isOccupied());
        assertEquals(SlotType.COMPACT, parkingSlot.get().getSlotType());
    }

    @Test
    void shouldThrowExceptionWhenVehicleAlreadyCheckedIn() {
        String licencePlate = "XYZ789";
        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate(licencePlate)
                .vehicleType(VehicleType.MOTORCYCLE.name())
                .build();

        // First check-in
        checkInService.checkIn(testParkingLot.getId(), checkInDto);

        // Second check-in should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkIn(testParkingLot.getId(), checkInDto));
    }

    @Test
    void shouldThrowExceptionWhenNoAvailableParkingSlot() {
        for (int i = 0; i < 3; i++) {
            CheckInDto checkInDto = CheckInDto.builder()
                    .licensePlate("FULL" + i)
                    .vehicleType(VehicleType.MOTORCYCLE.name())
                    .build();
            checkInService.checkIn(testParkingLot.getId(), checkInDto);
        }

        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate("NO_SLOT")
                .vehicleType(VehicleType.TRUCK.name())
                .build();

        assertThrows(IllegalStateException.class,
                () -> checkInService.checkIn(testParkingLot.getId(), checkInDto));
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateIsNullOrEmpty() {
        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate("")
                .vehicleType(VehicleType.CAR.name())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkIn(testParkingLot.getId(), checkInDto));

        checkInDto.setLicensePlate(null);
        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkIn(testParkingLot.getId(), checkInDto));
    }

    @Test
    void shouldThrowExceptionWhenVehicleTypeIsInvalid() {
        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate("INVALID")
                .vehicleType("INVALID_TYPE")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkIn(testParkingLot.getId(), checkInDto));
    }

    @Test
    void shouldCheckInMultipleVehicles() {
        CheckInDto carCheckIn = CheckInDto.builder()
                .licensePlate("CAR-001")
                .vehicleType(VehicleType.CAR.name())
                .build();
        CheckInResponseDTO carResponse = checkInService.checkIn(testParkingLot.getId(), carCheckIn);
        assertNotNull(carResponse);

        CheckInDto motorcycleCheckIn = CheckInDto.builder()
                .licensePlate("MOTO-001")
                .vehicleType(VehicleType.MOTORCYCLE.name())
                .build();
        CheckInResponseDTO motorcycleResponse = checkInService.checkIn(testParkingLot.getId(), motorcycleCheckIn);
        assertNotNull(motorcycleResponse);

        CheckInDto truckCheckIn = CheckInDto.builder()
                .licensePlate("TRUCK-001")
                .vehicleType(VehicleType.TRUCK.name())
                .build();
        CheckInResponseDTO truckResponse = checkInService.checkIn(testParkingLot.getId(), truckCheckIn);
        assertNotNull(truckResponse);

        assertEquals(3, checkInRepo.count());
        assertEquals(3, vehicleRepo.count());
        assertEquals(3, parkingSlotRepo.count());
    }

    @Test
    void shouldCheckOutMultipleVehicles() {
        CheckInDto carCheckIn = CheckInDto.builder()
                .licensePlate("CAR-001")
                .vehicleType(VehicleType.CAR.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), carCheckIn);

        CheckInDto motorcycleCheckIn = CheckInDto.builder()
                .licensePlate("MOTO-001")
                .vehicleType(VehicleType.MOTORCYCLE.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), motorcycleCheckIn);

        CheckInDto truckCheckIn = CheckInDto.builder()
                .licensePlate("TRUCK-001")
                .vehicleType(VehicleType.TRUCK.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), truckCheckIn);

        assertEquals(3, checkInRepo.count());

        // Now check out all vehicles
        List<CheckIn> activeCheckIns = checkInRepo.findActiveCheckIns(testParkingLot.getId());
        for (CheckIn checkIn : activeCheckIns) {
            CheckOutResponseDTO checkOutResponseDTO = checkInService.checkOut(testParkingLot.getId(),
                    CheckOutDto.builder().licensePlate(checkIn.getVehicle().getLicensePlate()).build());
            assertNotNull(checkOutResponseDTO);
            assertNotNull(checkOutResponseDTO.getCheckInDate());
            assertNotNull(checkOutResponseDTO.getCheckOutDate());
            assertNotNull(checkOutResponseDTO.getFee());
        }

        assertEquals(0, checkInRepo.findActiveCheckIns(testParkingLot.getId()).size());
    }

    @Test
    void shouldGetActiveCheckIns() {
        CheckInDto carCheckIn = CheckInDto.builder()
                .licensePlate("CAR-001")
                .vehicleType(VehicleType.CAR.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), carCheckIn);
        CheckInDto motorcycleCheckIn = CheckInDto.builder()
                .licensePlate("MOTO-001")
                .vehicleType(VehicleType.MOTORCYCLE.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), motorcycleCheckIn);
        CheckInDto truckCheckIn = CheckInDto.builder()
                .licensePlate("TRUCK-001")
                .vehicleType(VehicleType.TRUCK.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), truckCheckIn);
        List<CheckInResponseDTO> activeCheckIns = checkInService.getActiveCheckIns(testParkingLot.getId());
        assertNotNull(activeCheckIns);
        assertEquals(3, activeCheckIns.size());
        assertTrue(activeCheckIns.stream().anyMatch(checkIn -> checkIn.getLicensePlate().equals("CAR-001")));
        assertTrue(activeCheckIns.stream().anyMatch(checkIn -> checkIn.getLicensePlate().equals("MOTO-001")));
        assertTrue(activeCheckIns.stream().anyMatch(checkIn -> checkIn.getLicensePlate().equals("TRUCK-001")));
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveCheckIns() {
        List<CheckInResponseDTO> activeCheckIns = checkInService.getActiveCheckIns(testParkingLot.getId());
        assertNotNull(activeCheckIns);
        assertTrue(activeCheckIns.isEmpty(), "Expected no active check-ins, but found some.");
    }

    @Test
    void shouldThrowExceptionWhenLicensePlateIsNullOnCheckOut() {
        CheckInDto checkInDto = CheckInDto.builder()
                .licensePlate("TEST-001")
                .vehicleType(VehicleType.CAR.name())
                .build();
        checkInService.checkIn(testParkingLot.getId(), checkInDto);

        CheckOutDto checkOutDto = CheckOutDto.builder()
                .licensePlate(null)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkOut(testParkingLot.getId(), checkOutDto));
    }

    @Test
    void shouldThrowExceptionWhenVehicleNotFoundOnCheckOut() {
        CheckOutDto checkOutDto = CheckOutDto.builder()
                .licensePlate("NON_EXISTENT")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> checkInService.checkOut(testParkingLot.getId(), checkOutDto));
    }
}