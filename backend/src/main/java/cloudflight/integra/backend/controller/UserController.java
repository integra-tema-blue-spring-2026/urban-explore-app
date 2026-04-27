package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.user.UserLoginDto;
import cloudflight.integra.backend.model.dtos.user.UserRegisterDto;
import cloudflight.integra.backend.model.dtos.user.UserUpdateDto;
import cloudflight.integra.backend.model.dtos.user.UserViewDto;
import cloudflight.integra.backend.model.utils.mappers.rules.UserMappingRules;
import cloudflight.integra.backend.service.JwtService;
import cloudflight.integra.backend.service.UsersService;
import cloudflight.integra.backend.service.utils.Mapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class UserController {

    private final UsersService service;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
        UsersService service,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        PasswordEncoder passwordEncoder
    ) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // GET --------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<UserViewDto> getUser(@PathVariable UUID id) {
        return service.findById(id)
            .map(UserMappingRules.TO_DTO_RULE)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Set<UserViewDto>> getFollowers(@PathVariable UUID id) {
        return service.findById(id)
                .map(user -> user.getFollowers().stream()
                .map(follower -> Mapper.toDto(follower, UserMappingRules.TO_DTO_RULE))
                .collect(Collectors.toSet()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Set<UserViewDto>> getFollowing(@PathVariable UUID id) {
        return service.findById(id)
                .map(user -> user.getFollowing().stream()
                .map(following -> Mapper.toDto(following, UserMappingRules.TO_DTO_RULE))
                .collect(Collectors.toSet()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST --------------------------------------------------
    @PostMapping("/auth/register")
    public ResponseEntity<UserViewDto> registerUser(@Valid @RequestBody UserRegisterDto registerDto) {
        User newUser = Mapper.toEntity(registerDto, UserMappingRules.TO_ENTITY_RULE);
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        User savedUser = service.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Mapper.toDto(savedUser, UserMappingRules.TO_DTO_RULE));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("token", jwtService.generateToken(loginDto.getUsername())));
    }

    //PUT --------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<UserViewDto> updateUser(
        @PathVariable UUID id,
        @Valid @RequestBody UserUpdateDto updateDto){

        User currentUser = service.findById(id).orElse(null);
        if(currentUser == null){
            return ResponseEntity.notFound().build();
        }

        Mapper.mapUpdateToEntity(updateDto, currentUser, UserMappingRules.UPDATE_ENTITY_RULE);
        User updatedUser = service.save(currentUser);

        return ResponseEntity.ok(Mapper.toDto(updatedUser, UserMappingRules.TO_DTO_RULE));
    }

    @PutMapping("/follow/{id}")
    public ResponseEntity<UserViewDto> followUser(@RequestParam UUID followerId, @PathVariable UUID id) {
        User updatedUser = service.followUser(followerId, id);
        return ResponseEntity.ok(Mapper.toDto(updatedUser, UserMappingRules.TO_DTO_RULE));
    }

    @PutMapping("/unfollow/{id}")
    public ResponseEntity<UserViewDto> unfollowUser(@RequestParam UUID followerId, @PathVariable UUID id) {
        User updatedUser = service.unfollowUser(followerId, id);
        return ResponseEntity.ok(Mapper.toDto(updatedUser, UserMappingRules.TO_DTO_RULE));
    }

    // DELETE --------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        if(service.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test-error")
    public ResponseEntity<String> testError() throws Exception {
        throw new Exception("This is a simulated database crash!");
    }
}
