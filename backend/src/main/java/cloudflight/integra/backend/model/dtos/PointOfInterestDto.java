package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.PointOfInterestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointOfInterestDto {

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Type is required")
    private PointOfInterestType type;

    @NotNull(message = "City ID is required")
    private Long cityId;
}
