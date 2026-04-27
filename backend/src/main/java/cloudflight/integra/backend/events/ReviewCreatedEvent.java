package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.Review;

public record ReviewCreatedEvent(Review review) {
}
