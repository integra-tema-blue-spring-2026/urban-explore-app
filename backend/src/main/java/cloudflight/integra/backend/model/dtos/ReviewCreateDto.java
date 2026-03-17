package cloudflight.integra.backend.model.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDto {

    @NotBlank(message = "Review text cannot be blank")
    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String text;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull(message = "Review must have an user ID")
    private Long userId;

    @NotNull(message = "Review must have a Point of Interest ID")
    private Long poiId;
}
