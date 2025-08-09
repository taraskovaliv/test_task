package dev.kovaliv.test_task.data.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponseDTO {
    private Timestamp checkInDate;
    private Timestamp checkOutDate;

    private BigDecimal fee;

    @JsonGetter
    public String getDuration() {
        if (checkInDate != null && checkOutDate != null) {
            long durationMillis = checkOutDate.getTime() - checkInDate.getTime();
            long seconds = (durationMillis / 1000) % 60;
            long minutes = (durationMillis / (1000 * 60)) % 60;
            long hours = (durationMillis / (1000 * 60 * 60)) % 24;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return "00:00:00";
    }
}
