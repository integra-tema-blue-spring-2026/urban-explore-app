package cloudflight.integra.backend.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
@Data
public abstract class Entity<ID extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7331115341259248461L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;
}
