package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.dtos.city.CreateCityDto;
import cloudflight.integra.backend.model.dtos.city.UpdateCityDto;
import cloudflight.integra.backend.model.dtos.city.CityDto;
import cloudflight.integra.backend.model.utils.mappers.CityMapper;
import cloudflight.integra.backend.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;
    private final CityMapper cityMapper;


    @GetMapping
    public List<CityDto> getAllCities() {
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
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
}
