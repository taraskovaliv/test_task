package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.AddParkingLevelDTO;
import dev.kovaliv.test_task.data.dto.ParkingLevelDTO;
import dev.kovaliv.test_task.service.ParkingLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-levels")
public class ParkingLevelController {

    private final ParkingLevelService parkingLevelService;

    @PostMapping
    public ResponseEntity<ParkingLevelDTO> create(@RequestBody AddParkingLevelDTO parkingLevelDTO) {
        return ResponseEntity.ok(parkingLevelService.createParkingLevel(parkingLevelDTO));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingLevelService.deleteParkingLevel(id);
        return ResponseEntity.noContent().build();
    }
}
