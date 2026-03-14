package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "users")
@Data
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded
    private UserBio bio;

    private String avatarUrl;
}
