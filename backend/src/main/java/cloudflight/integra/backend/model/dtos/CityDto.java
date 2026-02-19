package cloudflight.integra.backend.model.dtos;

import cloudflight.integra.backend.model.CityStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto {
    private Long id;
    private String name;
    private String country;
    private String description;
    private Integer population;
    private String imageUrl;
    private CityStatus status;
}
