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

    public UserViewDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.role = user.getRole();
            this.bio = user.getBio();
            this.avatarUrl = user.getAvatarUrl();
        }
    }

    public static Function<User, UserViewDto> getDtoMapper() {
        return UserViewDto::new;
    }
}
