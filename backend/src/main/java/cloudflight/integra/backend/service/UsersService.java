package cloudflight.integra.backend.service;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService{

    protected final JpaRepository<User, UUID> repository;


    public UsersService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public User save(User t) {
        return repository.save(t);
    }


    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
