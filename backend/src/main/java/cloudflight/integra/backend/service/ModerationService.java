package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestStatus;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final CityRepository cityRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    @Transactional
    public void updateStatus(String entity, UUID id, String status) {
        String normalizedEntity = entity.toLowerCase();

        if ("city".equals(normalizedEntity)) {
            updateCityStatus(id, status);
        } 
        else if ("poi".equals(normalizedEntity)) {
            updatePointOfInterestStatus(id, status);
        } 
        else {
            throw new IllegalArgumentException("Invalid entity type: " + entity);
        }
    }

    private void updateCityStatus(UUID id, String status) {
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new CityNotFoundException("City not found with id: " + id));

        city.setStatus(CityStatus.valueOf(status.toUpperCase()));
        cityRepository.save(city);
    }

    private void updatePointOfInterestStatus(UUID id, String status) {
        PointOfInterest pointOfInterest = pointOfInterestRepository.findById(id)
            .orElseThrow(() -> new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!"));

        pointOfInterest.setStatus(PointOfInterestStatus.valueOf(status.toUpperCase()));
        pointOfInterestRepository.save(pointOfInterest);
    }
}