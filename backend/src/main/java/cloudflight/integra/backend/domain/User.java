package cloudflight.integra.backend.domain;

import cloudflight.integra.backend.domain.utils.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@jakarta.persistence.Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends Entity<Long> {


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
