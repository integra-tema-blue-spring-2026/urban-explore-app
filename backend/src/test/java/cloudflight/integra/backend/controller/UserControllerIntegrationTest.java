package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.BackendApplication;
import cloudflight.integra.backend.model.dtos.user.UserLoginDto;
import cloudflight.integra.backend.model.dtos.user.UserRegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:integration-tests-db;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUserReturnsCreatedUser() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);

        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail("jane." + suffix + "@example.com");
        registerDto.setUsername("jane_" + suffix);
        registerDto.setPassword("secret123");

        mockMvc.perform(post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(registerDto.getEmail()))
            .andExpect(jsonPath("$.username").value(registerDto.getUsername()))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void loginUserReturnsToken() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        String username = "jane_" + suffix;
        String email = "jane." + suffix + "@example.com";

        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail(email);
        registerDto.setUsername(username);
        registerDto.setPassword("secret123");

        mockMvc.perform(post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isCreated());

        UserLoginDto loginDto = new UserLoginDto(username, "secret123");

        mockMvc.perform(post("/users/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginUserWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        String username = "jane_" + suffix;
        String email = "jane." + suffix + "@example.com";

        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setEmail(email);
        registerDto.setUsername(username);
        registerDto.setPassword("secret123");

        mockMvc.perform(post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isCreated());

        UserLoginDto loginDto = new UserLoginDto(username, "wrong-password");

        mockMvc.perform(post("/users/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isUnauthorized());
    }
}





