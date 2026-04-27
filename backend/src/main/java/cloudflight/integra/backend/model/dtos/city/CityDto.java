package cloudflight.integra.backend.model.dtos.city;

import cloudflight.integra.backend.model.Coordinates;
import cloudflight.integra.backend.model.utils.enums.CityStatus;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto {
    private UUID id;
    private String name;
    private String country;
    private String description;
    private Integer population;
    private String imageUrl;
    private CityStatus status;
    private Coordinates coordinates;
}
