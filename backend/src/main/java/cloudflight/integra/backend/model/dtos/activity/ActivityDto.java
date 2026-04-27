package cloudflight.integra.backend.model.dtos.activity;

import cloudflight.integra.backend.model.utils.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDto {
    private UUID id;
    private UUID userId;
    private String username;
    private String userAvatarUrl;
    private ActivityType activityType;
    private String actionDescription; // Human-readable action like "left a 5-star review"
    private UUID targetId;
    private String targetType; // "Review", "PointOfInterest", "Quest"
    private String targetName;
    private String targetData; // Additional context data
    private LocalDateTime createdAt;
}
