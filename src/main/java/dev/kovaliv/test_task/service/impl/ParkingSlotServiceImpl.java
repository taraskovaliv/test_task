package dev.kovaliv.test_task.service.impl;

import dev.kovaliv.test_task.data.dto.AddParkingSlotDTO;
import dev.kovaliv.test_task.data.dto.ParkingSlotDTO;
import dev.kovaliv.test_task.data.entity.ParkingLevel;
import dev.kovaliv.test_task.data.entity.ParkingSlot;
import dev.kovaliv.test_task.data.repo.ParkingLevelRepo;
import dev.kovaliv.test_task.data.repo.ParkingSlotRepo;
import dev.kovaliv.test_task.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ModelMapper modelMapper;
    private final ParkingSlotRepo parkingSlotRepo;
    private final ParkingLevelRepo parkingLevelRepo;

    @Override
    @Transactional
    public List<ParkingSlotDTO> addSlotsToLevel(AddParkingSlotDTO addParkingSlotDTO) {
        if (addParkingSlotDTO.getParkingLevelId() == null) {
            throw new IllegalArgumentException("Parking level ID cannot be null.");
        }
        if (addParkingSlotDTO.getNumberOfSlots() <= 0) {
            throw new IllegalArgumentException("Number of slots must be greater than 0.");
        }

        Optional<ParkingLevel> parkingLevelOpt = parkingLevelRepo.findById(addParkingSlotDTO.getParkingLevelId());
        if (parkingLevelOpt.isEmpty()) {
            throw new IllegalArgumentException("Parking level with id " + addParkingSlotDTO.getParkingLevelId() + " does not exist.");
        }

        ParkingLevel parkingLevel = parkingLevelOpt.get();
        List<ParkingSlot> newSlots = new ArrayList<>();

        for (int i = 0; i < addParkingSlotDTO.getNumberOfSlots(); i++) {
            ParkingSlot slot = ParkingSlot.builder()
                    .occupied(false)
                    .slotType(addParkingSlotDTO.getSlotType())
                    .parkingLevel(parkingLevel)
                    .build();
            newSlots.add(slot);
        }

        List<ParkingSlot> savedSlots = parkingSlotRepo.saveAll(newSlots);
        
        return savedSlots.stream()
                .map(slot -> {
                    ParkingSlotDTO dto = modelMapper.map(slot, ParkingSlotDTO.class);
                    dto.setParkingLevelId(slot.getParkingLevel().getId());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public void removeSlot(Long slotId) {
        if (slotId == null) {
            throw new IllegalArgumentException("Slot ID cannot be null.");
        }
        
        Optional<ParkingSlot> slotOpt = parkingSlotRepo.findById(slotId);
        if (slotOpt.isEmpty()) {
            throw new IllegalArgumentException("Parking slot with id " + slotId + " does not exist.");
        }
        
        ParkingSlot slot = slotOpt.get();
        if (slot.isOccupied()) {
            throw new IllegalArgumentException("Cannot remove occupied parking slot.");
        }
        
        parkingSlotRepo.deleteById(slotId);
    }

    @Override
    @Transactional
    public void updateSlotAvailability(Long slotId, boolean occupied) {
        if (slotId == null) {
            throw new IllegalArgumentException("Slot ID cannot be null.");
        }
        parkingSlotRepo.findById(slotId).ifPresentOrElse(parkingSlot -> {
            parkingSlot.setOccupied(occupied);
            parkingSlotRepo.save(parkingSlot);
        }, () -> {
            throw new IllegalArgumentException("Parking slot with id " + slotId + " does not exist.");
        });
    }
}