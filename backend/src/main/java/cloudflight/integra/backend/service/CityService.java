package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.UpdateCityException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import cloudflight.integra.backend.model.dtos.city.CityDto;
import cloudflight.integra.backend.model.dtos.city.CreateCityDto;
import cloudflight.integra.backend.model.dtos.city.UpdateCityDto;
import cloudflight.integra.backend.model.utils.mappers.CityMapper;
import cloudflight.integra.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    private boolean isStringEmpty(String str) {
        return str == null || str.isBlank();
    }

    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
            .map(cityMapper::toDto)
            .collect(Collectors.toList());
    }

    public CityDto createCity(CreateCityDto cityDto) {
        City city = cityMapper.toEntity(cityDto);
        city.setStatus(CityStatus.PENDING);
        City savedCity = cityRepository.save(city);
        return cityMapper.toDto(savedCity);
    }

    @Transactional
    public CityDto updateCity(UUID id, UpdateCityDto inputCity) {
        if (isStringEmpty(inputCity.getDescription()) && isStringEmpty(inputCity.getImageUrl())) {
            throw new UpdateCityException("At least one field (description or imageUrl) must be provided for update.");
        }
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();
            cityMapper.updateEntity(inputCity, city);
            City updatedCity = cityRepository.save(city);
            return cityMapper.toDto(updatedCity);
        } else {
            throw new CityNotFoundException("City not found with id: " + id);
        }
    }

    @Transactional
    public void deleteCity(UUID id) {
        if (!cityRepository.existsById(id)) {
            throw new CityNotFoundException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }
}
