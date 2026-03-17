package cloudflight.integra.backend.model.dtos.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {
    @NotBlank(message = "Review text cannot be blank")
    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String text;

    @Min(1)
    @Max(5)
    private Integer rating;
}
