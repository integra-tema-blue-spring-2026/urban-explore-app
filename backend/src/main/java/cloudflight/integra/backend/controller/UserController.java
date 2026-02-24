package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.domain.dtos.UserCreateDto;
import cloudflight.integra.backend.domain.dtos.UserUpdateDto;
import cloudflight.integra.backend.domain.dtos.UserViewDto;
import cloudflight.integra.backend.domain.utils.UserMappingRules;
import cloudflight.integra.backend.service.UsersService;
import cloudflight.integra.backend.service.utils.Mapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UsersService service;

    public UserController(UsersService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserViewDto> getUser(@PathVariable Long id) {
        return service.findById(id)
            .map(UserMappingRules.TO_DTO_RULE)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<UserViewDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        User newUser = Mapper.toEntity(createDto, UserMappingRules.TO_ENTITY_RULE);

        User savedUser = service.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Mapper.toDto(savedUser, UserMappingRules.TO_DTO_RULE));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserViewDto> updateUser(@PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateDto updateDto){
        User currentUser = service.findById(id).orElse(null);
        if(currentUser == null){
            return ResponseEntity.notFound().build();
        }

        Mapper.mapUpdateToEntity(updateDto, currentUser, UserMappingRules.UPDATE_ENTITY_RULE);
        User updatedUser = service.save(currentUser);

        return ResponseEntity.ok(Mapper.toDto(updatedUser, UserMappingRules.TO_DTO_RULE));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
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
