package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.AUTO;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_levels")
@EqualsAndHashCode(exclude = {"parkingLot", "slots"})
@ToString(exclude = {"parkingLot", "slots"})
public class ParkingLevel {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private Integer floor;

    @ManyToOne
    private ParkingLot parkingLot;

    @OneToMany(mappedBy = "parkingLevel", cascade = ALL, orphanRemoval = true)
    private Set<ParkingSlot> slots;
}
