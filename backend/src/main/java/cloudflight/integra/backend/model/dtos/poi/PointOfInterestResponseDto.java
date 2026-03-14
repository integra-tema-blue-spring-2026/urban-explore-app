package cloudflight.integra.backend.model.dtos.poi;

import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;

import java.util.UUID;

public record PointOfInterestResponseDto(
    UUID id,
    String name,
    String description,
    String address,
    PointOfInterestType type,
    UUID cityId
){}
