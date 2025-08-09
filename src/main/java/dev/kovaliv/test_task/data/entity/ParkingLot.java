package dev.kovaliv.test_task.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.GenerationType.AUTO;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parking_lots")
@EqualsAndHashCode(exclude = {"levels"})
@ToString(exclude = {"levels"})
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParkingLevel> levels;
}
