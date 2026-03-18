package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.CreateCityDto;
import cloudflight.integra.backend.model.dtos.UpdateCityDto;
import cloudflight.integra.backend.model.dtos.CityDto;
import cloudflight.integra.backend.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/cities")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

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
}
