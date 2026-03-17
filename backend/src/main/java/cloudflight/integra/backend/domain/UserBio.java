package cloudflight.integra.backend.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Embeddable
public class UserBio {

    private String header;

    @Lob
    private String body;
    private String footer;

}
