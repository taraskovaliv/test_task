package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.AUTO;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private ParkingSlot parkingSlot;

    @Column(name = "check_in_date", nullable = false, columnDefinition = "TIMESTAMP")
    private Timestamp checkInDate;

    @Column(name = "check_out_date", columnDefinition = "TIMESTAMP")
    private Timestamp checkOutDate;

    @Column(name = "fee", columnDefinition = "DECIMAL(10, 2)")
    private BigDecimal fee;
}
