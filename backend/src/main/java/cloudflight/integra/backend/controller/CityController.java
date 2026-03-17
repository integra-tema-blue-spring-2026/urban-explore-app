package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.CreateCityDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.dtos.UpdateCityDto;
import cloudflight.integra.backend.model.dtos.CityDto;
import cloudflight.integra.backend.model.mappers.CityMapper;
import cloudflight.integra.backend.model.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final PointOfInterestMapper pointOfInterestMapper;

    @GetMapping
    public List<CityDto> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    public CityDto createCity(@Valid @RequestBody CreateCityDto cityDto) {
        return cityService.createCity(cityDto);
    }

    @PutMapping("/{id}")
    public CityDto updateCity(@PathVariable UUID id, @Valid @RequestBody UpdateCityDto cityDto) {
        return cityService.updateCity(id, cityDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable UUID id) {
        cityService.deleteCity(id);
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<CityDto> getCitiesByName(@PathVariable String name) {
        return cityService.getCitiesByName(name)
            .stream()
            .map(cityMapper::toDto)
            .toList();
    }

    @GetMapping("/{name}/pois")
    @ResponseStatus(HttpStatus.OK)
    public List<PointOfInterestResponseDto> getPointsOfInterestFromCityWithName
        (@PathVariable String name,
         @RequestParam(required = false) String poiDescription,
         @RequestParam(required = false) String poiName
        ) {

        Predicate<PointOfInterest> filter = poi -> true;

        if(poiDescription != null) {
            filter = filter.and(poi -> poi.getDescription().contains(poiDescription));
        }

        if(poiName != null) {
            filter = filter.and(poi -> poi.getName().equalsIgnoreCase(poiName));
        }

        return cityService.getCitiesByName(name)
            .stream()
            .flatMap(city -> city.getPointOfInterests().stream())
            .filter(filter)
            .map(pointOfInterestMapper::toDto)
            .toList();
    }
}
