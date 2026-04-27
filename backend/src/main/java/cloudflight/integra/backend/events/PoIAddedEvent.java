package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.User;

import java.util.UUID;

public record PoIAddedEvent(UUID userId, PointOfInterest point) {
}
