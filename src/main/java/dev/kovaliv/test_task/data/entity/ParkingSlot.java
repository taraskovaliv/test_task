package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_slots")
@EqualsAndHashCode(exclude = {"parkingLevel", "checkIns"})
@ToString(exclude = {"parkingLevel", "checkIns"})
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parking_slot_seq")
    @SequenceGenerator(name = "parking_slot_seq", sequenceName = "parking_slot_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "slot_type", nullable = false)
    private SlotType slotType;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean occupied;

    @ManyToOne
    private ParkingLevel parkingLevel;

    @OneToMany(mappedBy = "parkingSlot")
    private Set<CheckIn> checkIns;
}

