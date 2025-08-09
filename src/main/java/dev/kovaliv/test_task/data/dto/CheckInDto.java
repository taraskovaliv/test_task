package dev.kovaliv.test_task.data.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDto {
    private String vehicleType;
    private String licensePlate;
}
