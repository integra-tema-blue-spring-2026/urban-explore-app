package cloudflight.integra.backend.model.dtos.review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    @NotNull(message = "Review must have an ID")
    private UUID id;

    @NotBlank(message = "Review text cannot be blank")
    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String text;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull(message = "Review must have a posted date")
    private LocalDateTime postedDate;

    @NotNull(message = "Review must have an user ID")
    private UUID userId;

    @NotNull(message = "Review must have a Point of Interest ID")
    private UUID poiId;
}
