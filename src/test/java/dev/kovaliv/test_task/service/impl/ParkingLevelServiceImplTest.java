package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.AddParkingLevelDTO;
import dev.kovaliv.test_task.data.dto.ParkingLevelDTO;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.repo.ParkingLevelRepo;
import dev.kovaliv.test_task.data.repo.ParkingLotRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParkingLevelServiceImplTest {

    @Autowired
    private ParkingLevelServiceImpl parkingLevelService;

    @Autowired
    private ParkingLotRepo parkingLotRepo;

    @Autowired
    private ParkingLevelRepo parkingLevelRepo;

    private ParkingLot testParkingLot;

    @BeforeEach
    void setUp() {
        parkingLotRepo.deleteAll();
        parkingLevelRepo.deleteAll();

        ParkingLot parkingLot = ParkingLot.builder()
                .name("Test Lot")
                .levels(new HashSet<>())
                .build();
        testParkingLot = parkingLotRepo.save(parkingLot);
    }

    @Test
    void shouldCreateParkingLevel() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(1);
        addParkingLevelDTO.setParkingLotId(testParkingLot.getId());

        ParkingLevelDTO createdLevel = parkingLevelService.createParkingLevel(addParkingLevelDTO);

        assertNotNull(createdLevel);
        assertEquals(1, createdLevel.getFloor());
        assertEquals(testParkingLot.getId(), createdLevel.getParkingLotId());
    }

    @Test
    void shouldThrowExceptionWhenCreatingLevelWithNegativeFloor() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(-1);
        addParkingLevelDTO.setParkingLotId(testParkingLot.getId());

        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.createParkingLevel(addParkingLevelDTO));
    }

    @Test
    void shouldThrowExceptionWhenCreatingLevelWithNullFloor() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(null);
        addParkingLevelDTO.setParkingLotId(testParkingLot.getId());

        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.createParkingLevel(addParkingLevelDTO));
    }

    @Test
    void shouldThrowExceptionWhenCreatingLevelWithExistingFloor() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(1);
        addParkingLevelDTO.setParkingLotId(testParkingLot.getId());

        parkingLevelService.createParkingLevel(addParkingLevelDTO);

        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.createParkingLevel(addParkingLevelDTO));
    }

    @Test
    void shouldThrowExceptionWhenCreatingLevelForNonExistentParkingLot() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(1);
        addParkingLevelDTO.setParkingLotId(999L); // Non-existent ID

        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.createParkingLevel(addParkingLevelDTO));
    }

    @Test
    void shouldDeleteParkingLevel() {
        AddParkingLevelDTO addParkingLevelDTO = new AddParkingLevelDTO();
        addParkingLevelDTO.setFloor(1);
        addParkingLevelDTO.setParkingLotId(testParkingLot.getId());

        ParkingLevelDTO createdLevel = parkingLevelService.createParkingLevel(addParkingLevelDTO);
        assertNotNull(createdLevel);

        parkingLevelService.deleteParkingLevel(createdLevel.getId());

        assertFalse(parkingLevelRepo.existsById(createdLevel.getId()));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentParkingLevel() {
        Long nonExistentId = 999L; // Non-existent ID

        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.deleteParkingLevel(nonExistentId));
    }

    @Test
    void shouldThrowExceptionWhenDeletingParkingLevelWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> parkingLevelService.deleteParkingLevel(null));
    }
}