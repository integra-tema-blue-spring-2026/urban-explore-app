package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.city.CreateCityDto;
import cloudflight.integra.backend.model.dtos.city.UpdateCityDto;
import cloudflight.integra.backend.model.dtos.city.CityDto;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import cloudflight.integra.backend.model.utils.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.utils.mappers.CityMapper;
import cloudflight.integra.backend.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/all")
    public List<CityDto> getAllCities() {
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
    }

    @GetMapping
    public List<CityDto> getAllCitiesByStatus(
        @RequestParam(required = false) CityStatus status,
        Authentication authentication) {

        CityStatus effectiveStatus = resolveStatusFilterForCurrentUser(status, authentication);
        return cityService.getCitiesByStatus(effectiveStatus).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CityDto> getPendingCities() {
        return cityService.getCitiesByStatus(CityStatus.PENDING).stream().map(cityMapper::toDto).toList();
    }
    @PostMapping
    public CityDto createCity(@Valid @RequestBody CreateCityDto cityDto) {
        City createdCity =  cityService.createCity(cityMapper.toEntity(cityDto));
        return cityMapper.toDto(createdCity);
    }

    @PutMapping("/{id}")
    public CityDto updateCity(@PathVariable UUID id, @Valid @RequestBody UpdateCityDto cityDto) {
        City inputCity = cityMapper.toEntity(cityDto);
        return cityMapper.toDto(cityService.updateCity(id, inputCity));
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
    public List<PointOfInterestResponseDto> getPointsOfInterestFromCityWithName(
        @PathVariable String name,
        @RequestParam(required = false) String poiDescription,
        @RequestParam(required = false) String poiName) {

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

    private CityStatus resolveStatusFilterForCurrentUser(CityStatus requestedStatus, Authentication authentication) {
        if (isAdmin(authentication)) {
            return requestedStatus;
        }

        if (requestedStatus != null && requestedStatus != CityStatus.APPROVED) {
            throw new AccessDeniedException("Only admins can query non-approved cities.");
        }

        return CityStatus.APPROVED;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
            && authentication.getAuthorities() != null
            && authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
