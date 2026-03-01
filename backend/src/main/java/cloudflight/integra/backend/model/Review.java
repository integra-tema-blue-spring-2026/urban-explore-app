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

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false)
    private Long poiId;

    @Builder
    public Review(UUID id, String text, Integer rating, LocalDateTime postedDate, Long userId, Long poiId) {
        this.id = id;
        this.text = text;
        this.rating = rating;
        this.postedDate = postedDate;
        this.userId = userId;
        this.poiId = poiId;
    }
}
