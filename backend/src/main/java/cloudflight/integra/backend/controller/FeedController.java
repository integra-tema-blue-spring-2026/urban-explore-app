package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.activity.ActivityDto;
import cloudflight.integra.backend.model.utils.mappers.ActivityMapper;
import cloudflight.integra.backend.service.ActivityService;
import cloudflight.integra.backend.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FeedController {
    private final ActivityService activityService;
    private final UsersService usersService;
    private final ActivityMapper activityMapper;

    @GetMapping
    public ResponseEntity<?> getFeed() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
                log.warn("Unauthorized feed request - no authentication");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Please log in first");
            }

            String username = authentication.getName();
            log.info("Fetching feed for user: {}", username);

            Optional<User> userOptional = usersService.findByUsername(username);

            if (userOptional.isEmpty()) {
                log.warn("User not found: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: User not found");
            }

            User user = userOptional.get();

            List<ActivityDto> activities = activityService.getFriendsActivities(user.getId())
                .stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toList());

            log.info("Successfully fetched {} activities for user: {}", activities.size(), user.getId());

            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching feed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + e.getMessage());
        }
    }
}

