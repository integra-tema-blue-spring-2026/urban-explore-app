package cloudflight.integra.backend.service;

import cloudflight.integra.backend.events.CityAddedEvent;
import cloudflight.integra.backend.events.PoIAddedEvent;
import cloudflight.integra.backend.events.ReviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    @EventListener
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {

    }

    @EventListener
    public void handleCityAddedEvent(CityAddedEvent event) {

    }

    @EventListener
    public void handlePoIAddedEvent(PoIAddedEvent event) {

    }
}
