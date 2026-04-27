package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.City;

public record CityAddedEvent(City city) {
}
