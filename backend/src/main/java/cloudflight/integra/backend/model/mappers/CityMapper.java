package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.CityStatus;
import cloudflight.integra.backend.model.dtos.CityDto;
import cloudflight.integra.backend.model.dtos.CreateCityDto;
import cloudflight.integra.backend.model.dtos.UpdateCityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityMapper {

    public City toEntity(CityDto cityDto) {
        if (cityDto == null) {
            return null;
        }
        return City.builder()
            .id(cityDto.getId())
            .name(cityDto.getName())
            .country(cityDto.getCountry())
            .description(cityDto.getDescription())
            .population(cityDto.getPopulation())
            .imageUrl(cityDto.getImageUrl())
            .status(cityDto.getStatus())
            .build();
    }

    public CityDto toDto(City city) {
        if (city == null) {
            return null;
        }
        return CityDto.builder()
            .id(city.getId())
            .name(city.getName())
            .country(city.getCountry())
            .description(city.getDescription())
            .population(city.getPopulation())
            .imageUrl(city.getImageUrl())
            .status(city.getStatus())
            .build();
    }

    public void updateEntity(UpdateCityDto dto, City existingCity) {
        if (dto == null || existingCity == null) return;
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            existingCity.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            existingCity.setImageUrl(dto.getImageUrl());
        }
        }

    public City toEntity(CreateCityDto dto) {
        if (dto == null) return null;
        return City.builder()
            .name(dto.getName())
            .country(dto.getCountry())
            .description(dto.getDescription())
            .population(dto.getPopulation())
            .imageUrl(dto.getImageUrl())
            .status(CityStatus.PENDING)
            .build();
    }
}


