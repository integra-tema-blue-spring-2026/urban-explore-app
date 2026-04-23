package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.model.dtos.user.UserLoginDto;
import cloudflight.integra.backend.service.JwtService;
import cloudflight.integra.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class for controller integration tests.
 * Three things must be stubbed to make JWT auth work end-to-end without a
 * real database user:
 *  1. AuthenticationManager.authenticate() — called by the login endpoint.
 *     Without this stub the real AM queries the DB, finds no user, and the
 *     login endpoint returns 500.
 *  2. JwtService.generateToken() — called by the login endpoint after a
 *     successful authentication to produce the token string.
 *  3. JwtService.extractUsername() + isTokenValid() — called by JwtAuthFilter
 *     on EVERY subsequent authenticated request to validate the bearer token.
 *     Without these the filter calls UsersService.loadUserByUsername() with
 *     the username extracted from the token; if that user doesn't exist in H2
 *     the filter throws UsernameNotFoundException → 401 on every request.
 *  4. UsersService.loadUserByUsername() — called by JwtAuthFilter after it
 *     extracts and validates the username from the token, to load full
 *     UserDetails for the SecurityContext. The mock must return a real
 *     UserDetails object (not null) for the filter chain to proceed.
 * All four stubs are set up in setupAuthAndGetToken(). Call it at the top of
 * every @BeforeEach in subclasses and attach the returned token to requests.
 */
public abstract class BaseControllerIntegrationTest {

    static final String TEST_USERNAME = "testuser";
    static final String TEST_PASSWORD = "password123";
    static final String FAKE_TOKEN    = "fake-jwt-token-for-integration-tests";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected UsersService usersService;

    protected String setupAuthAndGetToken() {
        UserDetails fakeUserDetails = new User(
            TEST_USERNAME,
            TEST_PASSWORD,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // --- Stub 1: login endpoint ---
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(TEST_USERNAME);
        when(authenticationManager.authenticate(
            any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // --- Stub 2: token generation (login endpoint) ---
        when(jwtService.generateToken(TEST_USERNAME)).thenReturn(FAKE_TOKEN);

        // --- Stub 3: token validation (JwtAuthFilter, every request) ---
        when(jwtService.extractUsername(FAKE_TOKEN)).thenReturn(TEST_USERNAME);
        when(jwtService.isTokenValid(eq(FAKE_TOKEN), any(UserDetails.class))).thenReturn(true);

        // --- Stub 4: user loading (JwtAuthFilter, every request) ---
        when(usersService.loadUserByUsername(TEST_USERNAME)).thenReturn(fakeUserDetails);

        // Hit the real login endpoint through the full security stack
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/users/auth/login", loginDto, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException(
                "Failed to obtain token from /auth/login: "
                    + response.getStatusCode() + " " + response.getBody());
        }

        return (String) response.getBody().get("token");
    }

    protected HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected <T> HttpEntity<T> authEntity(T body, String token) {
        return new HttpEntity<>(body, authHeaders(token));
    }

    protected HttpEntity<Void> authEntity(String token) {
        return new HttpEntity<>(authHeaders(token));
    }
}
