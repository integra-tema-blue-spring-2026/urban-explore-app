package cloudflight.integra.backend.model.utils.mappers;

import cloudflight.integra.backend.model.Activity;
import cloudflight.integra.backend.model.dtos.activity.ActivityDto;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDto toDto(Activity activity) {
        return ActivityDto.builder()
            .id(activity.getId())
            .userId(activity.getUser() != null ? activity.getUser().getId() : null)
            .username(activity.getUser() != null ? activity.getUser().getUsername() : null)
            .userAvatarUrl(activity.getUser() != null ? activity.getUser().getAvatarUrl() : null)
            .activityType(activity.getType())
            .actionDescription(activity.getType().getDescription())
            .targetId(activity.getTargetId())
            .targetType(activity.getTargetType())
            .targetName(activity.getTargetName())
            .targetData(activity.getTargetData())
            .createdAt(activity.getCreatedAt())
            .build();
    }

    public Activity toEntity(ActivityDto activityDto) {
        return Activity.builder()
            .id(activityDto.getId())
            .type(activityDto.getActivityType())
            .targetId(activityDto.getTargetId())
            .targetType(activityDto.getTargetType())
            .targetName(activityDto.getTargetName())
            .targetData(activityDto.getTargetData())
            .createdAt(activityDto.getCreatedAt())
            .build();
    }
}
