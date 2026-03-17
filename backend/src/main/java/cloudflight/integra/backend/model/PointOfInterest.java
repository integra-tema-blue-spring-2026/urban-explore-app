package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
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
}
