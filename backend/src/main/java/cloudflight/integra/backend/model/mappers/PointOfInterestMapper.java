package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.PointOfInterestCreateDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestDto;
import org.springframework.stereotype.Component;

@Component
public class PointOfInterestMapper {

    public PointOfInterestDto toDto(PointOfInterest entity) {
        return new PointOfInterestDto(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getAddress(),
            entity.getType(),
            entity.getCityId()
        );
    }

    public PointOfInterest toNewEntity(PointOfInterestCreateDto dto) {
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
