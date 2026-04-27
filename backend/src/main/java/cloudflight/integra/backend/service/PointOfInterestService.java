package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointOfInterestService {
    private final PointOfInterestRepository pointOfInterestRepository;
    private final CityRepository cityRepository;
    private final ActivityService activityService;

    @Transactional
    public PointOfInterest save(PointOfInterest newPointOfInterest) {
        if (!cityRepository.existsById(newPointOfInterest.getCity().getId())){
            throw new CityNotFoundException("City with id " + newPointOfInterest.getCity().getId() + " not found");
        }

        PointOfInterest savedPoi = pointOfInterestRepository.save(newPointOfInterest);
        
        if (newPointOfInterest.getCreator() != null) {
            activityService.createPoiActivity(savedPoi, newPointOfInterest.getCreator());
        }
        
        return savedPoi;
    }

    @Transactional(readOnly = true)
    public List<PointOfInterest> getAll(){
        return pointOfInterestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PointOfInterest findById(UUID id) {
        return pointOfInterestRepository.findById(id).orElseThrow(
            () -> new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!")
        );
    }

    @Transactional
    public PointOfInterest update(PointOfInterest updatedPointOfInterest) {
        if(!pointOfInterestRepository.existsById(updatedPointOfInterest.getId())) {
            throw new PointOfInterestNotFoundException(
                "PointOfInterest with id " + updatedPointOfInterest.getId() + " not found!");
        }

        if (!cityRepository.existsById(updatedPointOfInterest.getCity().getId())){
            throw new CityNotFoundException("City with id " + updatedPointOfInterest.getCity().getId() + " not found");
        }

        return pointOfInterestRepository.save(updatedPointOfInterest);
    }

    @Transactional
    public void deleteById(UUID id) {
        if(!pointOfInterestRepository.existsById(id)) {
            throw new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!");
        }

        pointOfInterestRepository.deleteById(id);
    }
}
