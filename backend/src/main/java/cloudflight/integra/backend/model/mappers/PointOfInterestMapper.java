package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PointOfInterestMapper {

    public PointOfInterestResponseDto toDto(PointOfInterest entity) {
        return new PointOfInterestResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getAddress(),
            entity.getType(),
            entity.getCityId()
        );
    }

    public PointOfInterest toNewEntity(PointOfInterestRequestDto dto) {
        return new PointOfInterest(
            null,
            dto.getName(),
            dto.getDescription(),
            dto.getAddress(),
            dto.getType(),
            dto.getCityId()
        );
    }
}
