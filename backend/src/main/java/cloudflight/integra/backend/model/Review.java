package cloudflight.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}
