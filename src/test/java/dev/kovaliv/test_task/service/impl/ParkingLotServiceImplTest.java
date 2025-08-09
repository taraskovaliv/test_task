package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.CreateParkingLotDTO;
import dev.kovaliv.test_task.data.dto.ParkingLotDTO;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.repo.ParkingLotRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParkingLotServiceImplTest {

    @Autowired
    private ParkingLotServiceImpl parkingLotService;

    @Autowired
    private ParkingLotRepo parkingLotRepo;

    @BeforeEach
    void setUp() {
        parkingLotRepo.deleteAll();
    }

    @Test
    void shouldCreateParkingLotSuccessfully() {
        String testParkingLotName = "Test Parking Lot";
        CreateParkingLotDTO dto = CreateParkingLotDTO.builder()
                .name(testParkingLotName)
                .build();

        ParkingLotDTO result = parkingLotService.createParkingLot(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testParkingLotName, result.getName());

        assertTrue(parkingLotRepo.existsByName(testParkingLotName));
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        CreateParkingLotDTO dto = CreateParkingLotDTO.builder()
                .name(null)
                .build();

        assertThatThrownBy(() -> parkingLotService.createParkingLot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking lot name cannot be null or empty.");
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        CreateParkingLotDTO dto = CreateParkingLotDTO.builder()
                .name("")
                .build();

        assertThatThrownBy(() -> parkingLotService.createParkingLot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking lot name cannot be null or empty.");
    }

    @Test
    void shouldThrowExceptionWhenNameAlreadyExists() {
        ParkingLot existingLot = ParkingLot.builder()
                .name("Duplicate Name")
                .build();
        parkingLotRepo.save(existingLot);

        CreateParkingLotDTO dto = CreateParkingLotDTO.builder()
                .name("Duplicate Name")
                .build();

        assertThatThrownBy(() -> parkingLotService.createParkingLot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking lot with this name already exists.");
    }

    @Test
    void shouldDeleteParkingLotSuccessfully() {
        ParkingLot parkingLot = ParkingLot.builder()
                .name("To Delete")
                .build();
        ParkingLot saved = parkingLotRepo.save(parkingLot);

        parkingLotService.deleteParkingLot(saved.getId());

        boolean exists = parkingLotRepo.existsById(saved.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentParkingLot() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> parkingLotService.deleteParkingLot(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking lot with id " + nonExistentId + " does not exist.");
    }

    @Test
    void shouldMapEntityToDTOCorrectly() {
        CreateParkingLotDTO dto = CreateParkingLotDTO.builder()
                .name("Mapping Test Lot")
                .build();

        ParkingLotDTO result = parkingLotService.createParkingLot(dto);

        ParkingLot entity = parkingLotRepo.findById(result.getId()).orElse(null);
        assertNotNull(entity);
        assertEquals(result.getName(), entity.getName());
        assertEquals(result.getId(), entity.getId());
    }

    @Test
    void shouldGetParkingLotById() {
        String testName = "Get By ID Test";
        ParkingLot parkingLot = ParkingLot.builder()
                .name(testName)
                .build();
        ParkingLot saved = parkingLotRepo.save(parkingLot);

        ParkingLot found = parkingLotService.getParkingLotById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals(testName, found.getName());
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistentParkingLot() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> parkingLotService.getParkingLotById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking lot with id " + nonExistentId + " does not exist.");
    }
}