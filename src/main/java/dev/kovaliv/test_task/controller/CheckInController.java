package dev.kovaliv.test_task.controller;

import dev.kovaliv.test_task.data.dto.CheckInDto;
import dev.kovaliv.test_task.data.dto.CheckInResponseDTO;
import dev.kovaliv.test_task.data.dto.CheckOutDto;
import dev.kovaliv.test_task.data.dto.CheckOutResponseDTO;
import dev.kovaliv.test_task.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping("/check-in/{lotId}")
    public ResponseEntity<CheckInResponseDTO> checkIn(@PathVariable Long lotId, @RequestBody CheckInDto checkInDto) {
        return ResponseEntity.ok(checkInService.checkIn(lotId, checkInDto));
    }

    @PostMapping("/check-out/{lotId}")
    public ResponseEntity<CheckOutResponseDTO> checkOut(@PathVariable Long lotId, @RequestBody CheckOutDto checkOutDto) {
        return ResponseEntity.ok(checkInService.checkOut(lotId, checkOutDto));
    }

    @GetMapping("/active-check-ins/{lotId}")
    public ResponseEntity<List<CheckInResponseDTO>> getActiveCheckIns(@PathVariable Long lotId) {
        return ResponseEntity.ok(checkInService.getActiveCheckIns(lotId));
    }
}
