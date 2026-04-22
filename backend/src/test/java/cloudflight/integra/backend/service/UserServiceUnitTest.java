package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.utils.enums.UserRole;
import cloudflight.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsersService usersService;

    private User follower;
    private User targetUser;
    private UUID followerId;
    private UUID targetUserId;

    @BeforeEach
    void setUp() {

        followerId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();

        follower = new User();
        follower.setId(followerId);
        follower.setEmail("follower@example.com");
        follower.setUsername("follower");
        follower.setPassword("pass");
        follower.setRole(UserRole.USER);

        targetUser = new User();
        targetUser.setId(targetUserId);
        targetUser.setEmail("target@example.com");
        targetUser.setUsername("target");
        targetUser.setPassword("pass");
        targetUser.setRole(UserRole.USER);
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));

        Optional<User> result = usersService.findById(followerId);

        assertTrue(result.isPresent());
        assertEquals(followerId, result.get().getId());
        verify(userRepository, times(1)).findById(followerId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotFound() {
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

        Optional<User> result = usersService.findById(followerId);

        assertTrue(result.isEmpty());
    }

    @Test
    void save_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(follower);

        User result = usersService.save(follower);

        assertNotNull(result);
        assertEquals("follower@example.com", result.getEmail());
        verify(userRepository, times(1)).save(follower);
    }

    @Test
    void delete_ShouldCallDeleteById() {
        usersService.delete(followerId);

        verify(userRepository, times(1)).deleteById(followerId);
    }

    @Test
    void followUser_ShouldAddFollowerAndReturnTargetUser() {
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = usersService.followUser(followerId, targetUserId);

        assertTrue(result.getFollowers().contains(follower));
        assertTrue(follower.getFollowing().contains(targetUser));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void followUser_ShouldThrowException_WhenFollowerNotFound() {
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

        UserFollowException exception = assertThrows(UserFollowException.class,
            () -> usersService.followUser(followerId, targetUserId));

        assertEquals("Follower not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void followUser_ShouldThrowException_WhenTargetUserNotFound() {
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        UserFollowException exception = assertThrows(UserFollowException.class,
            () -> usersService.followUser(followerId, targetUserId));

        assertEquals("Target User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void unfollowUser_ShouldRemoveFollowerRelationship() {
        targetUser.addFollower(follower);
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        usersService.unfollowUser(followerId, targetUserId);

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenFollowerNotFound() {
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

        UserUnfollowException exception = assertThrows(UserUnfollowException.class,
            () -> usersService.unfollowUser(followerId, targetUserId));

        assertEquals("Follower not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void unfollowUser_ShouldThrowException_WhenTargetUserNotFound() {
        when(userRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        UserUnfollowException exception = assertThrows(UserUnfollowException.class,
            () -> usersService.unfollowUser(followerId, targetUserId));

        assertEquals("Target User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
