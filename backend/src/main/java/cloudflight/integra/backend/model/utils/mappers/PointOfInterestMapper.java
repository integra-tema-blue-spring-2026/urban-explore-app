package cloudflight.integra.backend.model.utils.mappers;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointOfInterestMapper {

    public PointOfInterestResponseDto toDto(PointOfInterest entity) {
        return new PointOfInterestResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getAddress(),
            entity.getType(),
            entity.getStatus(),
            entity.getCity().getId()
        );
    }

    public PointOfInterest toEntity(PointOfInterestRequestDto dto) {
        City city = City.builder().id(dto.getCityId()).build();

        return new PointOfInterest(
            null,
            dto.getName(),
            dto.getDescription(),
            dto.getAddress(),
            dto.getType(),
            null,
            city
        );
    }
}
