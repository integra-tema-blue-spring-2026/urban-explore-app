package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointOfInterestService {
    private final PointOfInterestRepository pointOfInterestRepository;
    private final CityRepository cityRepository;

    @Transactional
    public PointOfInterest save(PointOfInterest newPointOfInterest) {
        if (!cityRepository.existsById(newPointOfInterest.getCity().getId())){
            throw new CityNotFoundException("City with id " + newPointOfInterest.getCity().getId() + " not found");
        }

        return pointOfInterestRepository.save(newPointOfInterest);
    }

    @Transactional(readOnly = true)
    public List<PointOfInterest> getAll(){
        return pointOfInterestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PointOfInterest> getFiltered(UUID cityId, String cityName, String poiDescription, String poiName) {
        Specification<PointOfInterest> specification = Specification.unrestricted();

        if (cityId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city").get("id"), cityId)
            );
        }

        if (cityName != null && !cityName.isBlank()) {
            specification = specification.and(
                (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("city").get("name"), "%" + cityName + "%")
            );
        }

        if (poiDescription != null && !poiDescription.isBlank()) {
            specification = specification.and(
                (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("description"), "%" + poiDescription + "%")
            );
        }

        if (poiName != null && !poiName.isBlank()) {
            specification = specification.and(
                (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("name"), "%" + poiName + "%")
            );
        }

        return pointOfInterestRepository.findAll(specification);
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
