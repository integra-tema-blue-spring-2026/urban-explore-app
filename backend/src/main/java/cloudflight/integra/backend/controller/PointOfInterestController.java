package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.utils.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.PointOfInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pois")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PointOfInterestController {
    private final PointOfInterestService pointOfInterestService;
    private final PointOfInterestMapper pointOfInterestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PointOfInterestResponseDto savePointOfInterest(
        @Valid @RequestBody PointOfInterestRequestDto pointOfInterestRequestDto) {

        PointOfInterest newPointOfInterest = pointOfInterestMapper.toEntity(pointOfInterestRequestDto);
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
    public PointOfInterestResponseDto findById(@PathVariable UUID id) {
        return pointOfInterestMapper.toDto(pointOfInterestService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PointOfInterestResponseDto update(
        @PathVariable UUID id,
        @Valid @RequestBody PointOfInterestRequestDto pointOfInterestRequestDto) {

        PointOfInterest updatedPointOfInterest = pointOfInterestMapper.toEntity(pointOfInterestRequestDto);
        updatedPointOfInterest.setId(id);
        PointOfInterest savedPointOfInterest = pointOfInterestService.update(updatedPointOfInterest);
        return pointOfInterestMapper.toDto(savedPointOfInterest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID id) {
        pointOfInterestService.deleteById(id);
    }

}
