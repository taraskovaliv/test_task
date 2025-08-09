package dev.kovaliv.test_task.data.dto;

import dev.kovaliv.test_task.data.entity.SlotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddParkingSlotDTO {
    private Long parkingLevelId;
    private SlotType slotType;
    private int numberOfSlots;
}