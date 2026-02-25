package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.PointOfInterestType;

public record PointOfInterestResponseDto(
    Long id,
    String name,
    String description,
    String address,
    PointOfInterestType type,
    Long cityId
){}
