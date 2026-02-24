package cloudflight.integra.backend.domain.utils;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.domain.dtos.UserCreateDto;
import cloudflight.integra.backend.domain.dtos.UserUpdateDto;
import cloudflight.integra.backend.domain.dtos.UserViewDto;

import java.util.function.BiConsumer;
import java.util.function.Function;

public final class UserMappingRules {

    private UserMappingRules() {
    }

    public static final Function<UserCreateDto, User> TO_ENTITY_RULE = dto -> {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(UserRole.USER);
        user.setBio(dto.getBio());
        return user;
    };

    public static final BiConsumer<UserUpdateDto, User> UPDATE_ENTITY_RULE = (dto, existingUser) -> {
        if (dto.getUsername() != null) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getBio() != null) {
            existingUser.setBio(dto.getBio());
        }
        if (dto.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(dto.getAvatarUrl());
        }
    };

    public static final Function<User, UserViewDto> TO_DTO_RULE = user -> {
        if (user == null) {
            return null;
        }
        UserViewDto dto = new UserViewDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());

        return dto;
    };
}
