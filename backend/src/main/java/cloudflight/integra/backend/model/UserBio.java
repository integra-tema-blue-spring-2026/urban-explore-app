package cloudflight.integra.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserBio {

    private String header;

    @Column(columnDefinition = "TEXT")
    private String body;
    private String footer;

}
