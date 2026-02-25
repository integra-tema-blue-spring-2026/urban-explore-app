package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.PointOfInterestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pois")
public class PointOfInterestController {
    private final PointOfInterestService pointOfInterestService;
    private final PointOfInterestMapper pointOfInterestMapper;

    @Autowired
    public PointOfInterestController(PointOfInterestService pointOfInterestService,
                                     PointOfInterestMapper pointOfInterestMapper
    ) {
        this.pointOfInterestService = pointOfInterestService;
        this.pointOfInterestMapper = pointOfInterestMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PointOfInterestResponseDto savePointOfInterest(@Valid @RequestBody PointOfInterestRequestDto pointOfInterestRequestDto) {
        PointOfInterest newPointOfInterest = pointOfInterestMapper.toNewEntity(pointOfInterestRequestDto);
        PointOfInterest savedPointOfInterest = pointOfInterestService.save(newPointOfInterest);
        return pointOfInterestMapper.toDto(savedPointOfInterest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PointOfInterestResponseDto> getAll() {
        return pointOfInterestService
            .getAll()
            .stream()
            .map(pointOfInterestMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PointOfInterestResponseDto findById(@PathVariable Long id) {
        return pointOfInterestMapper.toDto(pointOfInterestService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PointOfInterestResponseDto update(@PathVariable Long id, @Valid @RequestBody PointOfInterestRequestDto pointOfInterestRequestDto) {
        PointOfInterest updatedPointOfInterest = pointOfInterestMapper.toNewEntity(pointOfInterestRequestDto);
        updatedPointOfInterest.setId(id);
        PointOfInterest savedPointOfInterest = pointOfInterestService.update(updatedPointOfInterest);
        return pointOfInterestMapper.toDto(savedPointOfInterest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        pointOfInterestService.deleteById(id);
    }

}
