package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointOfInterestType type;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private City city;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User creator;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
