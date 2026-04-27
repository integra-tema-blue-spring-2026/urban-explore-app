package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;

import java.util.UUID;

public record ReviewCreatedEvent(UUID userId, Review review) {
}
