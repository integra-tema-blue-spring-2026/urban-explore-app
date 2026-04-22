package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.controller.UserController;
import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.UserBio;
import cloudflight.integra.backend.model.dtos.user.UserCreateDto;
import cloudflight.integra.backend.model.dtos.user.UserUpdateDto;
import cloudflight.integra.backend.model.utils.enums.UserRole;
import cloudflight.integra.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsersService usersService;

    private UUID userId;
    private UUID followerId;
    private User testUser;
    private User followerUser;

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
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USER);
        testUser.setBio(bio);
        testUser.setAvatarUrl("http://example.com/avatar.jpg");

        followerUser = new User();
        followerUser.setId(followerId);
        followerUser.setEmail("follower@example.com");
        followerUser.setUsername("followeruser");
        followerUser.setPassword("password123");
        followerUser.setRole(UserRole.USER);
    }

    @Test
    void getUser_ShouldReturn200WithMappedUserFields() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));


        mockMvc.perform(get("/users/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.avatarUrl").value("http://example.com/avatar.jpg"))
            .andExpect(jsonPath("$.bio.header").value("Explorer"))
            .andExpect(jsonPath("$.bio.body").value("I love visiting cities."))
            .andExpect(jsonPath("$.bio.footer").value("Cluj-Napoca"));
    }

    @Test
    void getUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", userId))
            .andExpect(status().isNotFound());
    }


    @Test
    void getFollowers_ShouldReturn200WithMappedFollowers() throws Exception {
        testUser.addFollower(followerUser);
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/{id}/followers", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("follower@example.com"))
            .andExpect(jsonPath("$[0].username").value("followeruser"))
            .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    void getFollowers_ShouldReturn200WithEmptySet_WhenNoFollowers() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/{id}/followers", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFollowers_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}/followers", userId))
            .andExpect(status().isNotFound());
    }


    @Test
    void getFollowing_ShouldReturn200WithMappedFollowing() throws Exception {
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());
        targetUser.setEmail("target@example.com");
        targetUser.setUsername("targetuser");
        targetUser.setPassword("pass");
        targetUser.setRole(UserRole.USER);
        targetUser.addFollower(testUser);

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/{id}/following", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("target@example.com"))
            .andExpect(jsonPath("$[0].username").value("targetuser"));
    }

    @Test
    void getFollowing_ShouldReturn200WithEmptySet_WhenNotFollowingAnyone() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/{id}/following", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getFollowing_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}/following", userId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldReturn201WithMappedUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("user@example.com");
        createDto.setUsername("testuser");
        createDto.setPassword("password123");

        when(usersService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.avatarUrl").value("http://example.com/avatar.jpg"));
    }

    @Test
    void createUser_ShouldAlwaysSetRoleToUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("user@example.com");
        createDto.setUsername("testuser");
        createDto.setPassword("password123");

        when(usersService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.role").value("USER"));
    }


    @Test
    void updateUser_ShouldReturn200WithUpdatedFields() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("updateduser");
        updateDto.setAvatarUrl("http://example.com/new-avatar.jpg");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("user@example.com");
        updatedUser.setUsername("updateduser");
        updatedUser.setPassword("password123");
        updatedUser.setRole(UserRole.USER);
        updatedUser.setAvatarUrl("http://example.com/new-avatar.jpg");

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        when(usersService.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.avatarUrl").value("http://example.com/new-avatar.jpg"));
    }

    @Test
    void updateUser_ShouldNotOverwriteFieldsWithNull() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setAvatarUrl("http://example.com/new-avatar.jpg");

        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        when(usersService.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("username");

        when(usersService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isNotFound());

        verify(usersService, never()).save(any());
    }


    @Test
    void followUser_ShouldReturn200WithMappedTargetUser() throws Exception {
        when(usersService.followUser(followerId, userId)).thenReturn(testUser);

        mockMvc.perform(put("/users/follow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void followUser_ShouldReturn400_WhenFollowerNotFound() throws Exception {
        when(usersService.followUser(followerId, userId))
            .thenThrow(new UserFollowException("Follower not found"));

        mockMvc.perform(put("/users/follow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void followUser_ShouldReturn400_WhenTargetUserNotFound() throws Exception {
        when(usersService.followUser(followerId, userId))
            .thenThrow(new UserFollowException("Target User not found"));

        mockMvc.perform(put("/users/follow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unfollowUser_ShouldReturn200WithMappedTargetUser() throws Exception {
        when(usersService.unfollowUser(followerId, userId)).thenReturn(testUser);

        mockMvc.perform(put("/users/unfollow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void unfollowUser_ShouldReturn400_WhenFollowerNotFound() throws Exception {
        when(usersService.unfollowUser(followerId, userId))
            .thenThrow(new UserUnfollowException("Follower not found"));

        mockMvc.perform(put("/users/unfollow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void unfollowUser_ShouldReturn400_WhenTargetUserNotFound() throws Exception {
        when(usersService.unfollowUser(followerId, userId))
            .thenThrow(new UserUnfollowException("Target User not found"));

        mockMvc.perform(put("/users/unfollow/{id}", userId)
                .param("followerId", followerId.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ShouldReturn204_WhenUserExists() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(usersService).delete(userId);

        mockMvc.perform(delete("/users/{id}", userId))
            .andExpect(status().isNoContent());

        verify(usersService, times(1)).delete(userId);
    }

    @Test
    void deleteUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(usersService.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{id}", userId))
            .andExpect(status().isNotFound());

        verify(usersService, never()).delete(any());
    }


    @Test
    void testError_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/users/test-error"))
            .andExpect(status().isInternalServerError());
    }
}
