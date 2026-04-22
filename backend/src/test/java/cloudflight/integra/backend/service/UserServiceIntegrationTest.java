package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.utils.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UsersService.class)
class UserServiceIntegrationTest {

    @Autowired
    private UsersService usersService;

    private User follower;
    private User targetUser;

    @BeforeEach
    void setUp() {
        follower = new User();
        follower.setEmail("follower@example.com");
        follower.setUsername("follower");
        follower.setPassword("pass");
        follower.setRole(UserRole.USER);

        targetUser = new User();
        targetUser.setEmail("target@example.com");
        targetUser.setUsername("target");
        targetUser.setPassword("pass");
        targetUser.setRole(UserRole.USER);

        follower = usersService.save(follower);
        targetUser = usersService.save(targetUser);
    }

    @Test
    void save_ShouldPersistUserWithGeneratedId() {
        assertNotNull(follower.getId());
        Optional<User> found = usersService.findById(follower.getId());
        assertTrue(found.isPresent());
        assertEquals("follower@example.com", found.get().getEmail());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotFound() {
        Optional<User> result = usersService.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void delete_ShouldRemoveUser() {
        usersService.delete(follower.getId());

        assertTrue(usersService.findById(follower.getId()).isEmpty());
    }

    @Test
    void followUser_ShouldPersistFollowerRelationship() {
        User updatedTarget = usersService.followUser(follower.getId(), targetUser.getId());

        assertTrue(updatedTarget.getFollowers().stream()
            .anyMatch(f -> f.getId().equals(follower.getId())));
        Optional<User> reloadedFollower = usersService.findById(follower.getId());
        assertTrue(reloadedFollower.isPresent());
        assertTrue(reloadedFollower.get().getFollowing().stream()
            .anyMatch(u -> u.getId().equals(targetUser.getId())));
    }

    @Test
    void followUser_ShouldThrowException_WhenFollowerNotFound() {
        assertThrows(UserFollowException.class,
            () -> usersService.followUser(UUID.randomUUID(), targetUser.getId()));
    }

    @Test
    void followUser_ShouldThrowException_WhenTargetNotFound() {
        assertThrows(UserFollowException.class,
            () -> usersService.followUser(follower.getId(), UUID.randomUUID()));
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenFollowerNotFound() {
        assertThrows(UserUnfollowException.class,
            () -> usersService.unfollowUser(UUID.randomUUID(), targetUser.getId()));
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenTargetNotFound() {
        assertThrows(UserUnfollowException.class,
            () -> usersService.unfollowUser(follower.getId(), UUID.randomUUID()));
    }
}
