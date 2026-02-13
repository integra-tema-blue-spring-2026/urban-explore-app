package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.PointOfInterestCreateDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestDto;
import cloudflight.integra.backend.model.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointOfInterestService {
    private final PointOfInterestRepository pointOfInterestRepository;
    private final PointOfInterestMapper pointOfInterestMapper;

    @Autowired
    public PointOfInterestService(
        PointOfInterestRepository pointOfInterestRepository,
        PointOfInterestMapper pointOfInterestMapper
    ) {
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.pointOfInterestMapper = pointOfInterestMapper;
    }

    @Transactional
    public PointOfInterestDto save(PointOfInterestCreateDto pointOfInterestCreateDto) {
        PointOfInterest newPointOfInterest = pointOfInterestMapper.toNewEntity(pointOfInterestCreateDto);
        PointOfInterest savedPointOfInterest = pointOfInterestRepository.save(newPointOfInterest);
        return pointOfInterestMapper.toDto(savedPointOfInterest);
    }

    @Transactional(readOnly = true)
    public List<PointOfInterestDto> getAll(){
        return pointOfInterestRepository.findAll()
            .stream()
            .map(pointOfInterestMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public PointOfInterestDto findById(Long id) {
        return pointOfInterestMapper.toDto(pointOfInterestRepository.findById(id).orElseThrow(
            () -> new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!")
        ));
    }

    @Transactional
    public void deleteById(Long id) {
        if(!pointOfInterestRepository.existsById(id)) {
            throw new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!");
        }

        pointOfInterestRepository.deleteById(id);
    }

    @Transactional
    public PointOfInterestDto update(Long id, PointOfInterestCreateDto pointOfInterestCreateDto) {
        if(!pointOfInterestRepository.existsById(id)) {
            throw new PointOfInterestNotFoundException("PointOfInterest with id " + id + " not found!");
        }

        PointOfInterest pointOfInterest = pointOfInterestMapper.toNewEntity(pointOfInterestCreateDto);
        pointOfInterest.setId(id);
        PointOfInterest savedPointOfInterest = pointOfInterestRepository.save(pointOfInterest);
        return pointOfInterestMapper.toDto(savedPointOfInterest);
    }
}
