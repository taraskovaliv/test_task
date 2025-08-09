package dev.kovaliv.test_task.data.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotDTO {
    private Long id;
    private String name;
}
