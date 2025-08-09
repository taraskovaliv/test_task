package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.AddParkingSlotDTO;
import dev.kovaliv.test_task.data.dto.ParkingSlotDTO;
import dev.kovaliv.test_task.data.entity.ParkingLevel;
import dev.kovaliv.test_task.data.entity.ParkingLot;
import dev.kovaliv.test_task.data.entity.ParkingSlot;
import dev.kovaliv.test_task.data.entity.SlotType;
import dev.kovaliv.test_task.data.repo.ParkingLevelRepo;
import dev.kovaliv.test_task.data.repo.ParkingLotRepo;
import dev.kovaliv.test_task.data.repo.ParkingSlotRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ParkingSlotServiceImplTest {

    @Autowired
    private ParkingSlotServiceImpl parkingSlotService;

    @Autowired
    private ParkingSlotRepo parkingSlotRepo;

    @Autowired
    private ParkingLevelRepo parkingLevelRepo;

    @Autowired
    private ParkingLotRepo parkingLotRepo;

    private ParkingLevel testParkingLevel;

    @BeforeEach
    void setUp() {
        parkingSlotRepo.deleteAll();
        parkingLevelRepo.deleteAll();
        parkingLotRepo.deleteAll();

        ParkingLot parkingLot = ParkingLot.builder()
                .name("Test Lot")
                .levels(new HashSet<>())
                .build();
        parkingLot = parkingLotRepo.save(parkingLot);

        testParkingLevel = ParkingLevel.builder()
                .floor(1)
                .parkingLot(parkingLot)
                .slots(new HashSet<>())
                .build();
        testParkingLevel = parkingLevelRepo.save(testParkingLevel);
    }

    @Test
    void shouldAddSlotsToLevelSuccessfully() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(testParkingLevel.getId())
                .slotType(SlotType.COMPACT)
                .numberOfSlots(5)
                .build();

        List<ParkingSlotDTO> result = parkingSlotService.addSlotsToLevel(dto);
        assertThat(result).hasSize(5);

        result.forEach(slot -> {
            assertNotNull(slot.getId());
            assertFalse(slot.isOccupied());
            assertEquals(SlotType.COMPACT, slot.getSlotType());
            assertEquals(testParkingLevel.getId(), slot.getParkingLevelId());
        });

        assertEquals(5, parkingSlotRepo.countByParkingLevelAndOccupied(testParkingLevel, false));
    }

    @Test
    void shouldThrowExceptionWhenParkingLevelIdIsNull() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(null)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(3)
                .build();

        assertThatThrownBy(() -> parkingSlotService.addSlotsToLevel(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking level ID cannot be null.");
    }

    @Test
    void shouldThrowExceptionWhenNumberOfSlotsIsZero() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(testParkingLevel.getId())
                .slotType(SlotType.COMPACT)
                .numberOfSlots(0)
                .build();

        assertThatThrownBy(() -> parkingSlotService.addSlotsToLevel(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of slots must be greater than 0.");
    }

    @Test
    void shouldThrowExceptionWhenNumberOfSlotsIsNegative() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(testParkingLevel.getId())
                .slotType(SlotType.COMPACT)
                .numberOfSlots(-1)
                .build();

        assertThatThrownBy(() -> parkingSlotService.addSlotsToLevel(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of slots must be greater than 0.");
    }

    @Test
    void shouldThrowExceptionWhenParkingLevelDoesNotExist() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(999L)
                .slotType(SlotType.COMPACT)
                .numberOfSlots(3)
                .build();

        assertThatThrownBy(() -> parkingSlotService.addSlotsToLevel(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking level with id 999 does not exist.");
    }

    @Test
    void shouldCreateSlotsWithDifferentSlotTypes() {
        for (SlotType slotType : SlotType.values()) {
            AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                    .parkingLevelId(testParkingLevel.getId())
                    .slotType(slotType)
                    .numberOfSlots(2)
                    .build();

            List<ParkingSlotDTO> result = parkingSlotService.addSlotsToLevel(dto);

            assertThat(result).hasSize(2);
            result.forEach(slot -> assertEquals(slotType, slot.getSlotType()));
        }
    }

    @Test
    void shouldRemoveSlotSuccessfully() {
        ParkingSlot slot = ParkingSlot.builder()
                .slotType(SlotType.COMPACT)
                .occupied(false)
                .parkingLevel(testParkingLevel)
                .build();
        ParkingSlot saved = parkingSlotRepo.save(slot);

        parkingSlotService.removeSlot(saved.getId());

        assertThat(parkingSlotRepo.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenRemovingSlotWithNullId() {
        assertThatThrownBy(() -> parkingSlotService.removeSlot(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Slot ID cannot be null.");
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentSlot() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> parkingSlotService.removeSlot(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking slot with id " + nonExistentId + " does not exist.");
    }

    @Test
    void shouldThrowExceptionWhenRemovingOccupiedSlot() {
        ParkingSlot occupiedSlot = ParkingSlot.builder()
                .slotType(SlotType.COMPACT)
                .occupied(true)
                .parkingLevel(testParkingLevel)
                .build();
        ParkingSlot saved = parkingSlotRepo.save(occupiedSlot);

        assertThatThrownBy(() -> parkingSlotService.removeSlot(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot remove occupied parking slot.");
    }

    @Test
    void shouldMaintainTransactionalIntegrity() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(testParkingLevel.getId())
                .slotType(SlotType.LARGE)
                .numberOfSlots(3)
                .build();

        List<ParkingSlotDTO> slots = parkingSlotService.addSlotsToLevel(dto);

        assertThat(slots).hasSize(3);

        Long firstSlotId = slots.getFirst().getId();
        parkingSlotService.removeSlot(firstSlotId);

        assertEquals(2, parkingSlotRepo.countByParkingLevelAndOccupied(testParkingLevel, false));
    }

    @Test
    void shouldMapDTOFieldsCorrectly() {
        AddParkingSlotDTO dto = AddParkingSlotDTO.builder()
                .parkingLevelId(testParkingLevel.getId())
                .slotType(SlotType.HANDICAPPED)
                .numberOfSlots(1)
                .build();

        ParkingSlotDTO slotDTO = parkingSlotService.addSlotsToLevel(dto).getFirst();

        ParkingSlot entity = parkingSlotRepo.findById(slotDTO.getId()).orElse(null);
        assertNotNull(entity);
        assertEquals(slotDTO.getSlotType(), entity.getSlotType());
        assertEquals(slotDTO.isOccupied(), entity.isOccupied());
        assertEquals(slotDTO.getParkingLevelId(), entity.getParkingLevel().getId());
    }

    @Test
    void shouldUpdateSlotAvailabilitySuccessfully() {
        ParkingSlot slot = ParkingSlot.builder()
                .slotType(SlotType.COMPACT)
                .occupied(false)
                .parkingLevel(testParkingLevel)
                .build();
        ParkingSlot saved = parkingSlotRepo.save(slot);

        parkingSlotService.updateSlotAvailability(saved.getId(), true);

        Optional<ParkingSlot> updatedSlotOpt = parkingSlotRepo.findById(saved.getId());
        assertTrue(updatedSlotOpt.isPresent());
        assertTrue(updatedSlotOpt.get().isOccupied());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAvailabilityOfNonExistentSlot() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> parkingSlotService.updateSlotAvailability(nonExistentId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parking slot with id " + nonExistentId + " does not exist.");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAvailabilityWithNullId() {
        assertThatThrownBy(() -> parkingSlotService.updateSlotAvailability(null, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Slot ID cannot be null.");
    }
}