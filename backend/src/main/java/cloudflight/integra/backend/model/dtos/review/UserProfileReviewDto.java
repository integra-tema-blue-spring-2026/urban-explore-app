package cloudflight.integra.backend.model.dtos.review;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserProfileReviewDto {
    private UUID id;
    private String text;
    private Integer rating;
    private LocalDateTime postedDate;
    private UUID poiId;
    private String poiName;
    private String poiImageUrl;
    private String cityName;
}
