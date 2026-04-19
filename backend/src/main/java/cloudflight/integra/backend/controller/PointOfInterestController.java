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
import java.util.function.Predicate;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PointOfInterestResponseDto> getPointsOfInterestWithFilter(
        @RequestParam(required = false) String cityId,
        @RequestParam(required = false) String cityName,
        @RequestParam(required = false) String poiDescription,
        @RequestParam(required = false) String poiName
    ){

        Predicate<PointOfInterest> filter = poi -> true;

        if(cityId != null) {
            filter = filter.and(poi -> poi.getCity().getId().toString().equals(cityId));
        }

        if(cityName != null) {
            filter = filter.and(poi -> poi.getCity().getName().equals(cityName));
        }

        if(poiDescription != null) {
            filter = filter.and(poi -> poi.getDescription().contains(poiDescription));
        }

        if(poiName != null) {
            filter = filter.and(poi -> poi.getName().equalsIgnoreCase(poiName));
        }

        return pointOfInterestService.getAll().stream()
            .filter(filter)
            .map(pointOfInterestMapper::toDto)
            .toList();
    }

}
