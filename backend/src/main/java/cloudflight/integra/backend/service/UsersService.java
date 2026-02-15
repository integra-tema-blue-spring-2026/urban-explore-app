package cloudflight.integra.backend.service;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.domain.dtos.UserViewDto;
import cloudflight.integra.backend.repository.UserRepository;
import cloudflight.integra.backend.service.utils.DtoMapper;
import org.springframework.stereotype.Service;

@Service
public class UsersService extends EntityService<User, Long>{

    public UsersService(UserRepository userRepository) {
        super(userRepository);
    }

    public UserViewDto getUserDto(User user){
        return DtoMapper.toDto(user, UserViewDto.getDtoMapper());
    }
}
