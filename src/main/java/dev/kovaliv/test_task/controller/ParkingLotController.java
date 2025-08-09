package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.CreateParkingLotDTO;
import dev.kovaliv.test_task.data.dto.ParkingLotDTO;
import dev.kovaliv.test_task.service.ParkingLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @PostMapping
    public ResponseEntity<ParkingLotDTO> createParkingLot(@RequestBody CreateParkingLotDTO dto) {
        return ResponseEntity.ok(parkingLotService.createParkingLot(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable Long id) {
        parkingLotService.deleteParkingLot(id);
        return ResponseEntity.noContent().build();
    }
}
