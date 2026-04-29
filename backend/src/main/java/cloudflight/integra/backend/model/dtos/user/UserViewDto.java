package cloudflight.integra.backend.model.dtos.user;

import cloudflight.integra.backend.model.UserBio;
import cloudflight.integra.backend.model.utils.enums.UserRole;
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
    private Integer xpPoints;
}
