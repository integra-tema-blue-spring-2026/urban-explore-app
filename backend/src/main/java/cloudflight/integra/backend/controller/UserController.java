package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.domain.User;
import cloudflight.integra.backend.domain.dtos.UserCreateDto;
import cloudflight.integra.backend.domain.dtos.UserUpdateDto;
import cloudflight.integra.backend.domain.dtos.UserViewDto;
import cloudflight.integra.backend.service.UsersService;
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
            .map(service::getUserDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<UserViewDto> createUser(@Valid @RequestBody UserCreateDto createDto) {
        User newUser = createDto.toEntity();

        User savedUser = service.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.getUserDto(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserViewDto> updateUser(@PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateDto updateDto){
        User currentUser = service.findById(id).orElse(null);
        if(currentUser == null){
            return ResponseEntity.notFound().build();
        }

        updateDto.updateExistingEntity(currentUser);

        User updatedUser = service.save(currentUser);

        return ResponseEntity.ok(service.getUserDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserViewDto> deleteUser(@PathVariable Long id){
        if(service.findById(id).isPresent()){
            return ResponseEntity.notFound().build();
        }

        service.delete(id);

        return ResponseEntity.noContent().build();
    }


}
