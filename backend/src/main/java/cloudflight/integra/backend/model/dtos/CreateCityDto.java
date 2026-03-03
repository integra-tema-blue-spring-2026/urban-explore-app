package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.CityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCityDto {

    @NotBlank(message = "City name is required")
    private String name;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Description is required")
    private String description;
    @Positive(message = "Quantity must be positive")
    @NotNull(message = "Population is required")
    private Integer population;
    @NotBlank(message = "Image URL is required")
    private String imageUrl;


    private UUID id;
    private CityStatus status;
}
