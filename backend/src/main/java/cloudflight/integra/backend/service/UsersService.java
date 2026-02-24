package cloudflight.integra.backend.service;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersService extends EntityService<User, Long>{
    public UsersService(UserRepository userRepository) {
        super(userRepository);
    }
}
