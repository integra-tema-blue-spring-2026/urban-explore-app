package cloudflight.integra.backend.model.dtos.poi;

import cloudflight.integra.backend.model.Coordinates;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Type is required")
    private PointOfInterestType type;

    @NotNull(message = "City ID is required")
    private UUID cityId;

    @NotNull(message = "Coordinates are required")
    @Valid
    private Coordinates coordinates;
}
