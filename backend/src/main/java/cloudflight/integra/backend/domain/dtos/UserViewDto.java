package cloudflight.integra.backend.domain.dtos;

import cloudflight.integra.backend.domain.UserBio;
import cloudflight.integra.backend.domain.utils.UserRole;
import lombok.*;

import java.util.UUID;


@Data
public class UserViewDto {
    private UUID id;
    private String email;
    private String username;
    private UserRole role;
    private UserBio bio;
    private String avatarUrl;
}
