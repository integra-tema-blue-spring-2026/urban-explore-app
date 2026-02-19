package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.CityStatus;
import cloudflight.integra.backend.model.dtos.CityDto;
import cloudflight.integra.backend.model.mappers.CityMapper;
import cloudflight.integra.backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
            .map(cityMapper::toDto)
            .collect(Collectors.toList());
    }

    public CityDto createCity(CityDto cityDto) {
        City city = cityMapper.toEntity(cityDto);
        city.setStatus(CityStatus.PENDING);
        City savedCity = cityRepository.save(city);
        return cityMapper.toDto(savedCity);
    }

    @Transactional
    public CityDto updateCity(Long id, CityDto inputCity) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            City city = optionalCity.get();

            city.setDescription(inputCity.getDescription());
            city.setImageUrl(inputCity.getImageUrl());
            City updatedCity = cityRepository.save(city);
            return cityMapper.toDto(updatedCity);
        } else {
            throw new RuntimeException("City not found with id: " + id);
        }
    }

    @Transactional
    public void deleteCity(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new RuntimeException("City not found with id: " + id);
        }
        cityRepository.deleteById(id);
    }
}
