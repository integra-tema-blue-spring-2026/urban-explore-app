package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class UsersService implements UserDetailsService {
    protected final UserRepository repository;

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

    @Transactional
    public User followUser(UUID followerId, UUID targetUserId) {

        User follower = repository.findById(followerId).orElseThrow(
            () -> new UserFollowException("Follower not found"));
        User targetUser = repository.findById(targetUserId).orElseThrow(
            () -> new UserFollowException("Target User not found"));

        targetUser.addFollower(follower);

        repository.save(follower);
        return repository.save(targetUser);
    }

    @Transactional
    public User unfollowUser(UUID followerId, UUID targetUserId) {

        User follower = repository.findById(followerId).orElseThrow(
            () -> new UserUnfollowException("Follower not found"));
        User targetUser = repository.findById(targetUserId).orElseThrow(
            () -> new UserUnfollowException("Target User not found"));

        follower.removeFollower(targetUser);

        repository.save(follower);
        return repository.save(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("User not found with username: " + username)
        );
    }
}
