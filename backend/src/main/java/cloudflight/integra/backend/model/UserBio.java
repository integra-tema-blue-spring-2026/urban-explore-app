package cloudflight.integra.backend.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Embeddable
public class UserBio {

    private String header;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String footer;

}
