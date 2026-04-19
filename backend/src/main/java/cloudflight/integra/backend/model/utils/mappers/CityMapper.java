package cloudflight.integra.backend.model.utils.mappers;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import cloudflight.integra.backend.model.dtos.city.CityDto;
import cloudflight.integra.backend.model.dtos.city.CreateCityDto;
import cloudflight.integra.backend.model.dtos.city.UpdateCityDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityMapper {

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
            .coordinates(city.getCoordinates())
            .build();
    }

    public City toEntity(UpdateCityDto dto) {
        if (dto == null) return null;
        return City.builder()
            .description(dto.getDescription())
            .imageUrl(dto.getImageUrl())
            .coordinates(dto.getCoordinates())
            .build();
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
            .coordinates(dto.getCoordinates())
            .build();
    }
}


