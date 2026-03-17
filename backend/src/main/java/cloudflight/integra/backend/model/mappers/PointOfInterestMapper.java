package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.exception.CityNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestResponseDto;
import cloudflight.integra.backend.repository.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointOfInterestMapper {

    private final CityRepository cityRepository;

    public PointOfInterestResponseDto toDto(PointOfInterest entity) {
        return new PointOfInterestResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getAddress(),
            entity.getType(),
            entity.getCity().getId()
        );
    }

    public PointOfInterest toEntity(PointOfInterestRequestDto dto) {
        City city = cityRepository.findById(dto.getCityId()).orElseThrow(
            () -> new CityNotFoundException("City with id " + dto.getCityId() + " not found")
        );

        return new PointOfInterest(
            null,
            dto.getName(),
            dto.getDescription(),
            dto.getAddress(),
            dto.getType(),
            city
        );
    }
}
