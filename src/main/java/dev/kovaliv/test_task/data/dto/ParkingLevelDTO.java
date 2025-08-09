package dev.kovaliv.test_task.data.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLevelDTO {
    private Long id;
    private Integer floor;
    private Long parkingLotId;
}
