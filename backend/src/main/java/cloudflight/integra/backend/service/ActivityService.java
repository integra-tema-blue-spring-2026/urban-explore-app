package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.Activity;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.utils.enums.ActivityType;
import cloudflight.integra.backend.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;

    @Transactional
    public void createReviewActivity(Review review) {
        try {
            String actionDescription = String.format("%d-star review", review.getRating());
            
            Activity activity = Activity.builder()
                .user(review.getUser())
                .type(ActivityType.REVIEW_CREATED)
                .targetId(review.getId())
                .targetType("Review")
                .targetName(review.getPointOfInterest() != null ? 
                    review.getPointOfInterest().getName() : "Unknown POI")
                .targetData(String.valueOf(review.getRating()))
                .build();
            
            activityRepository.save(activity);
            log.info("Created REVIEW_CREATED activity for user: {}", review.getUser().getId());
        } catch (Exception e) {
            log.error("Failed to create review activity", e);
        }
    }

    @Transactional
    public void createPoiActivity(PointOfInterest poi, User creator) {
        try {
            Activity activity = Activity.builder()
                .user(creator)
                .type(ActivityType.POI_CREATED)
                .targetId(poi.getId())
                .targetType("PointOfInterest")
                .targetName(poi.getName())
                .targetData(poi.getType().toString())
                .build();
            
            activityRepository.save(activity);
            log.info("Created POI_CREATED activity for user: {}", creator.getId());
        } catch (Exception e) {
            log.error("Failed to create POI activity", e);
        }
    }

    @Transactional
    public void createQuestCompletionActivity(UUID questId, String questTitle, User user) {
        try {
            Activity activity = Activity.builder()
                .user(user)
                .type(ActivityType.QUEST_COMPLETED)
                .targetId(questId)
                .targetType("Quest")
                .targetName(questTitle)
                .build();
            
            activityRepository.save(activity);
            log.info("Created QUEST_COMPLETED activity for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to create quest completion activity", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Activity> getFriendsActivities(UUID userId) {
        Pageable pageable = PageRequest.of(0, 20);
        
        List<Activity> activities = activityRepository.findFriendsActivities(userId, pageable);
        log.info("getFriendsActivities for user {}: found {} activities", userId, activities.size());
        activities.forEach(a -> log.info("  - Activity: type={}, user_id={}, target={}",
            a.getType(), a.getUser().getId(), a.getTargetName()));
        return activities;
    }

    @Transactional(readOnly = true)
    public List<Activity> getUserActivities(UUID userId) {
        return activityRepository.findByUserId(userId);
    }

    @Transactional
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }
}
