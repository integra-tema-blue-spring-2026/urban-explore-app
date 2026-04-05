package cloudflight.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name="reviews")
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private String text;

    @Column(nullable=false)
    private Integer rating;

    @Column(nullable=false)
    private LocalDateTime postedDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "poi_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PointOfInterest pointOfInterest;

    @Builder
    public Review(UUID id, String text, Integer rating, LocalDateTime postedDate, User user, PointOfInterest pointOfInterest) {
        this.id = id;
        this.text = text;
        this.rating = rating;
        this.postedDate = postedDate;
        this.user = user;
        this.pointOfInterest = pointOfInterest;
    }
}
