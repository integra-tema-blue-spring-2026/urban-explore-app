package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.User;

import java.util.UUID;

public record CityAddedEvent(UUID userId, City city) {
}
