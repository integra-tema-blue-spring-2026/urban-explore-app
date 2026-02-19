package cloudflight.integra.backend.domain.dtos;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.domain.UserBio;
import cloudflight.integra.backend.domain.utils.UserRole;
import lombok.*;

import java.util.function.Function;

@Data
public class UserViewDto {
    private Long id;
    private String email;
    private String username;
    private UserRole role;
    private UserBio bio;
    private String avatarUrl;
}
