package cloudflight.integra.backend.model.dtos.city;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCityDto {

    private String description;
    private String imageUrl;

}
