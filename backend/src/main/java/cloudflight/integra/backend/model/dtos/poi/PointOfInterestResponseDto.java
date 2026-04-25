package cloudflight.integra.backend.model.dtos.poi;

import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestStatus;

import java.util.UUID;

public record PointOfInterestResponseDto(
    UUID id,
    String name,
    String description,
    String address,
    PointOfInterestType type,
    PointOfInterestStatus status,
    UUID cityId
){}
