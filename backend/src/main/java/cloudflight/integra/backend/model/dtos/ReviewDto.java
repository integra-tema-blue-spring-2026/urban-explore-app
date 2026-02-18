package cloudflight.integra.backend.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private UUID id;

    private String text;

    private Integer rating;

    private LocalDateTime postedDate;

    private Long userId;

    private Long poiId;
}
