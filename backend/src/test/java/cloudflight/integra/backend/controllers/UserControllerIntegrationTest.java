package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.UserBio;
import cloudflight.integra.backend.model.dtos.user.UserLoginDto;
import cloudflight.integra.backend.model.dtos.user.UserRegisterDto;
import cloudflight.integra.backend.model.dtos.user.UserUpdateDto;
import cloudflight.integra.backend.model.utils.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerIntegrationTest extends BaseControllerIntegrationTest {


    private UUID userId;
    private UUID followerId;
    private User testUser;
    private User followerUser;
    private String token;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        followerId = UUID.randomUUID();

        UserBio bio = new UserBio();
        bio.setHeader("Explorer");
        bio.setBody("I love visiting cities.");
        bio.setFooter("Cluj-Napoca");

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("user@example.com");
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword("encodedpassword");
        testUser.setRole(UserRole.USER);
        testUser.setBio(bio);
        testUser.setAvatarUrl("http://example.com/avatar.jpg");

        followerUser = new User();
        followerUser.setId(followerId);
        followerUser.setEmail("follower@example.com");
        followerUser.setUsername("followeruser");
        followerUser.setPassword("encodedpassword");
        followerUser.setRole(UserRole.USER);

        when(usersService.save(any(User.class))).thenReturn(testUser);

        token = setupAuthAndGetToken();
    }


    @Test
    void register_ShouldReturn201WithMappedUser() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("new@example.com");
        dto.setUsername("newuser");
        dto.setPassword("password123");

        when(usersService.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/register", dto, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("email")).isEqualTo("user@example.com");
        assertThat(response.getBody().get("role")).isEqualTo("USER");
    }

    @Test
    void register_ShouldReturn409_WhenEmailAlreadyExists() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("duplicate@example.com");
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(usersService.save(any(User.class)))
            .thenThrow(new DataIntegrityViolationException("Email already exists"));

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/register", dto, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().get("message")).isEqualTo("Database Error");
    }

    @Test
    void register_ShouldReturn400_WhenBodyIsInvalid() {
        UserRegisterDto dto = new UserRegisterDto();

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/register", dto, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Validation Error");
        verify(usersService, never()).save(any(User.class));
    }


    @Test
    void login_ShouldReturn200AndToken_WhenCredentialsAreValid() {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername(TEST_USERNAME);
        dto.setPassword(TEST_PASSWORD);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/login", dto, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
        assertThat((String) response.getBody().get("token")).isEqualTo(FAKE_TOKEN);
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any()))
            .thenThrow(new org.springframework.security.authentication.BadCredentialsException(
                "Bad credentials"));

        UserLoginDto dto = new UserLoginDto();
        dto.setUsername(TEST_USERNAME);
        dto.setPassword("wrongpassword");

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/login", dto, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void getUser_ShouldReturn200WithMappedUserFields() {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<Map> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.GET, authEntity(token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(userId.toString());
        assertThat(response.getBody().get("email")).isEqualTo("user@example.com");
        assertThat(response.getBody().get("username")).isEqualTo(TEST_USERNAME);
        assertThat(response.getBody().get("role")).isEqualTo("USER");
        assertThat(response.getBody().get("avatarUrl")).isEqualTo("http://example.com/avatar.jpg");
        assertThat(((Map<?, ?>) response.getBody().get("bio")).get("header")).isEqualTo("Explorer");
    }

    @Test
    void getUser_ShouldReturn404_WhenUserNotFound() {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUser_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/users/" + userId, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void getFollowers_ShouldReturn200WithFollowers() {
        testUser.addFollower(followerUser);
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<List> response = restTemplate.exchange(
            "/users/" + userId + "/followers", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getFollowers_ShouldReturn200WithEmptySet_WhenNoFollowers() {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<List> response = restTemplate.exchange(
            "/users/" + userId + "/followers", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getFollowers_ShouldReturn404_WhenUserNotFound() {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/" + userId + "/followers", HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void getFollowing_ShouldReturn200WithFollowing() {
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());
        targetUser.setEmail("target@example.com");
        targetUser.setUsername("targetuser");
        targetUser.setPassword("pass");
        targetUser.setRole(UserRole.USER);
        targetUser.addFollower(testUser);

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        ResponseEntity<List> response = restTemplate.exchange(
            "/users/" + userId + "/following", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getFollowing_ShouldReturn404_WhenUserNotFound() {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/" + userId + "/following", HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void updateUser_ShouldReturn200WithUpdatedFields() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("updateduser");
        updateDto.setAvatarUrl("http://example.com/new-avatar.jpg");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("user@example.com");
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("encodedpassword");
        updatedUser.setRole(UserRole.USER);
        updatedUser.setAvatarUrl("http://example.com/new-avatar.jpg");

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        when(usersService.save(any(User.class))).thenReturn(updatedUser);

        ResponseEntity<Map> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.PUT, authEntity(updateDto, token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("username")).isEqualTo("updateduser");
        assertThat(response.getBody().get("avatarUrl")).isEqualTo("http://example.com/new-avatar.jpg");
    }

    @Test
    void updateUser_ShouldNotOverwriteFieldsWithNull() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setAvatarUrl("http://example.com/new-avatar.jpg");

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        when(usersService.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<Map> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.PUT, authEntity(updateDto, token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("username")).isEqualTo(TEST_USERNAME);
    }

    @Test
    void updateUser_ShouldReturn404_WhenUserNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("updateduser");

        when(usersService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.PUT, authEntity(updateDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(usersService, never()).save(any());
    }


    @Test
    void followUser_ShouldReturn200WithTargetUser() {
        when(usersService.followUser(followerId, userId)).thenReturn(testUser);

        ResponseEntity<Map> response = restTemplate.exchange(
            "/users/follow/" + userId + "?followerId=" + followerId,
            HttpMethod.PUT, authEntity(token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(userId.toString());
    }

    @Test
    void followUser_ShouldReturn400_WhenFollowerNotFound() {
        when(usersService.followUser(followerId, userId))
            .thenThrow(new UserFollowException("Follower not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/follow/" + userId + "?followerId=" + followerId,
            HttpMethod.PUT, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void followUser_ShouldReturn400_WhenTargetUserNotFound() {
        when(usersService.followUser(followerId, userId))
            .thenThrow(new UserFollowException("Target User not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/follow/" + userId + "?followerId=" + followerId,
            HttpMethod.PUT, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void unfollowUser_ShouldReturn200WithTargetUser() {
        when(usersService.unfollowUser(followerId, userId)).thenReturn(testUser);

        ResponseEntity<Map> response = restTemplate.exchange(
            "/users/unfollow/" + userId + "?followerId=" + followerId,
            HttpMethod.PUT, authEntity(token), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(userId.toString());
    }

    @Test
    void unfollowUser_ShouldReturn400_WhenFollowerNotFound() {
        when(usersService.unfollowUser(followerId, userId))
            .thenThrow(new UserUnfollowException("Follower not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/unfollow/" + userId + "?followerId=" + followerId,
            HttpMethod.PUT, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void deleteUser_ShouldReturn204_WhenUserExists() {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(usersService).delete(userId);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.DELETE, authEntity(token), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(usersService, times(1)).delete(userId);
    }

    @Test
    void deleteUser_ShouldReturn404_WhenUserNotFound() {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.exchange(
            "/users/" + userId, HttpMethod.DELETE, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(usersService, never()).delete(any());
    }

    @Test
    void testError_ShouldReturn500() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/users/test-error", HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
