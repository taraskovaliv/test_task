package dev.kovaliv.test_task.service;

import dev.kovaliv.test_task.data.dto.AddParkingSlotDTO;
import dev.kovaliv.test_task.data.dto.ParkingSlotDTO;

import java.util.List;

public interface ParkingSlotService {
    
    List<ParkingSlotDTO> addSlotsToLevel(AddParkingSlotDTO addParkingSlotDTO);
    
    void removeSlot(Long slotId);

    void updateSlotAvailability(Long slotId, boolean occupied);
}