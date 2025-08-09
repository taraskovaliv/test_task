package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.AddParkingSlotDTO;
import dev.kovaliv.test_task.data.dto.ParkingSlotDTO;
import dev.kovaliv.test_task.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-slots")
public class ParkingSlotController {
    
    private final ParkingSlotService parkingSlotService;
    
    @PostMapping
    public ResponseEntity<List<ParkingSlotDTO>> addSlotsToLevel(@RequestBody AddParkingSlotDTO addParkingSlotDTO) {
        return ResponseEntity.ok(parkingSlotService.addSlotsToLevel(addParkingSlotDTO));
    }

    @PostMapping("/{slotId}/availability")
    public ResponseEntity<Void> updateSlotAvailability(@PathVariable Long slotId, @RequestParam boolean occupied) {
        parkingSlotService.updateSlotAvailability(slotId, occupied);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<Void> removeSlot(@PathVariable Long slotId) {
        parkingSlotService.removeSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}