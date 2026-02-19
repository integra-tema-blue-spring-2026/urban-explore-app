package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.dtos.CityDto;
import cloudflight.integra.backend.service.CityService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @GetMapping
    public List<CityDto> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    public CityDto createCity(@RequestBody CityDto cityDto) {
        return cityService.createCity(cityDto);
    }

    @PutMapping("/{id}")
    public CityDto updateCity(@PathVariable Long id, @RequestBody CityDto cityDto) {
        return cityService.updateCity(id, cityDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
    }
}
