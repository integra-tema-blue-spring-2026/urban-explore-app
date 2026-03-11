package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.CityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer population;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CityStatus status;

    @OneToMany(mappedBy = "city")
    private List<PointOfInterest> pointOfInterests;
}
