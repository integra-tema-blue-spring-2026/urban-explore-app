package cloudflight.integra.backend.quests.model.entity;

import java.util.UUID;

import cloudflight.integra.backend.utils.Enums.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "quests")
public class Quest {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private UUID cityId;

    public Quest(UUID id, String title, String description, Difficulty difficulty, int exp, UUID cityId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.exp = exp;
        this.cityId = cityId;
    }

    public Quest() {}
}
