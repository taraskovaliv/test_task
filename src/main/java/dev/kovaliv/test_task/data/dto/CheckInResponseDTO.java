package dev.kovaliv.test_task.data.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDTO {
    private String vehicleType;
    private String licensePlate;

    private Timestamp entryTime;

    private Long slotId;
    private Long levelId;
    private Integer levelFlour;
}
