package cloudflight.integra.backend.service;

import cloudflight.integra.backend.events.CityAddedEvent;
import cloudflight.integra.backend.events.PoIAddedEvent;
import cloudflight.integra.backend.events.ReviewCreatedEvent;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ReviewRepository reviewRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    @EventListener
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {
         User user= userRepository.getReferenceById(event.userId());
         user.setXpPoints(user.getXpPoints()+5);
    }

    @EventListener
    public void handleCityAddedEvent(CityAddedEvent event) {
       User user= userRepository.getReferenceById(event.userId());
       user.setXpPoints(user.getXpPoints()+10);
    }

    @EventListener
    public void handlePoIAddedEvent(PoIAddedEvent event) {
        User user= userRepository.getReferenceById(event.userId());
        user.setXpPoints(user.getXpPoints()+7);
    }
}
