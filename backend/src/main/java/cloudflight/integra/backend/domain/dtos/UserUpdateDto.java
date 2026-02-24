package cloudflight.integra.backend.domain.dtos;

import cloudflight.integra.backend.domain.UserBio;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    private UserBio bio;
    private String avatarUrl;
}
