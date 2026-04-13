package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.user.UserLoginDto;
import cloudflight.integra.backend.model.dtos.user.UserRegisterDto;
import cloudflight.integra.backend.model.dtos.user.UserViewDto;
import cloudflight.integra.backend.model.utils.enums.UserRole;
import cloudflight.integra.backend.service.JwtService;
import cloudflight.integra.backend.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UsersService service;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController controller;

    @Test
    void registerUserEncodesPasswordAndReturnsCreated() {
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail("jane@example.com");
        registerDto.setUsername("jane_doe");
        registerDto.setPassword("secret123");

        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(service.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<UserViewDto> response = controller.registerUser(registerDto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(service).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("jane@example.com", savedUser.getEmail());
        assertEquals("jane_doe", savedUser.getUsername());
        assertEquals("encoded-secret", savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserViewDto body = response.getBody();
        assertNotNull(body);
        assertEquals("jane@example.com", body.getEmail());
    }

    @Test
    void loginUserReturnsToken() {
        UserLoginDto loginDto = new UserLoginDto("jane_doe", "secret123");
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtService.generateToken("jane_doe")).thenReturn("jwt-token");

        ResponseEntity<Map<String, String>> response = controller.loginUser(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().get("token"));
    }

    @Test
    void loginUserThrowsWhenAuthenticationFails() {
        UserLoginDto loginDto = new UserLoginDto("jane_doe", "wrong-pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> controller.loginUser(loginDto));
    }
}





