package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded
    private UserBio bio;

    private String avatarUrl;

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer xpPoints = 0;

    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private  Set<User> following = new HashSet<>();

    public void addFollower(User follower) {
        this.followers.add(follower);
        follower.getFollowing().add(this);
    }

    public void removeFollower(User follower) {
        this.followers.remove(follower);
        follower.getFollowing().remove(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //TODO: Implement authorities based on UserRole
        return List.of();
    }
}
