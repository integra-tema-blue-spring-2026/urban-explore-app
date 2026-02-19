package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.dtos.CityDto;
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
}
