package cloudflight.integra.backend.service;

import cloudflight.integra.backend.events.CityAddedEvent;
import cloudflight.integra.backend.events.PoIAddedEvent;
import cloudflight.integra.backend.events.ReviewCreatedEvent;
import cloudflight.integra.backend.events.QuestCompletedEvent;
import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.UserQuestProgress;
import cloudflight.integra.backend.model.utils.enums.QuestProgressStatus;
import cloudflight.integra.backend.repository.QuestRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.UserQuestProgressRepository;
import cloudflight.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private static final int REVIEW_XP = 5;
    private static final int CITY_XP = 10;
    private static final int POI_XP = 7;

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final QuestRepository questRepository;
    private final UserQuestProgressRepository userQuestProgressRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @TransactionalEventListener()
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {
        addXp(event.userId(), REVIEW_XP);
    }

    @TransactionalEventListener
    public void handleCityAddedEvent(CityAddedEvent event) {
       addXp(event.userId(), CITY_XP);
    }

    @TransactionalEventListener
    public void handlePoIAddedEvent(PoIAddedEvent event) {
        addXp(event.userId(), POI_XP);
    }

    @TransactionalEventListener
    public void handleQuestCompletedEvent(QuestCompletedEvent event) {
        addXp(event.userId(), event.quest().getExp());
    }

    private void addXp(UUID userId, int xpToAdd) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        int currentXp = user.getXpPoints() == null ? 0 : user.getXpPoints();
        user.setXpPoints(currentXp + xpToAdd);
        userRepository.save(user);
    }

    
}
