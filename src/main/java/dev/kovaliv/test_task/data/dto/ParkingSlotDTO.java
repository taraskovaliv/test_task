package dev.kovaliv.test_task.data.dto;

import dev.kovaliv.test_task.data.entity.SlotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotDTO {
    private Long id;
    private SlotType slotType;
    private boolean occupied;
    private Long parkingLevelId;
}