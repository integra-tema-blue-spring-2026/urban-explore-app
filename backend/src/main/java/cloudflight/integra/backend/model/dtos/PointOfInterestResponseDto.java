package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.PointOfInterestType;

import java.util.UUID;

public record PointOfInterestResponseDto(
    UUID id,
    String name,
    String description,
    String address,
    PointOfInterestType type,
    UUID cityId
){}
