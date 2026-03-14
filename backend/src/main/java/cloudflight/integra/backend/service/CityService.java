package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.UpdateCityException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    private boolean isStringEmpty(String str) {
        return str == null || str.isBlank();
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public City createCity(City city) {
        return cityRepository.save(city);
    }

    @Transactional
    public City updateCity(UUID id, City inputCity) {
        if (isStringEmpty(inputCity.getDescription()) && isStringEmpty(inputCity.getImageUrl())) {
            throw new UpdateCityException("At least one field (description or imageUrl) must be provided for update.");
        }
        Optional<City> existingCityOpt = cityRepository.findById(id);
        if (existingCityOpt.isPresent()) {
            City existingCity = existingCityOpt.get();
            existingCity.setDescription(inputCity.getDescription());
            existingCity.setImageUrl(inputCity.getImageUrl());
            return cityRepository.save(existingCity);
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
