package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointOfInterestService {
    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public PointOfInterestService(
        PointOfInterestRepository pointOfInterestRepository
    ) {
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @Transactional
    public PointOfInterest save(PointOfInterest newPointOfInterest) {
        return pointOfInterestRepository.save(newPointOfInterest);
    }

    @Transactional(readOnly = true)
    public List<PointOfInterest> getAll(){
        return pointOfInterestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PointOfInterest findById(Long id) {
        return pointOfInterestRepository.findById(id).orElseThrow(
            () -> new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!")
        );
    }

    @Transactional
    public PointOfInterest update(PointOfInterest updatedPointOfInterest) {
        if(!pointOfInterestRepository.existsById(updatedPointOfInterest.getId())) {
            throw new PointOfInterestNotFoundException("PointOfInterest with id " + updatedPointOfInterest.getId() + " not found!");
        }

        return pointOfInterestRepository.save(updatedPointOfInterest);
    }

    @Transactional
    public void deleteById(Long id) {
        if(!pointOfInterestRepository.existsById(id)) {
            throw new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!");
        }

        pointOfInterestRepository.deleteById(id);
    }
}
