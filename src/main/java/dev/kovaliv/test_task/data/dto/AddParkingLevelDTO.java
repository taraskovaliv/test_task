package dev.kovaliv.test_task.data.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddParkingLevelDTO {
    private Long parkingLotId;
    private Integer floor;
}
