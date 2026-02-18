package cloudflight.integra.backend.quests.model.entity;

import cloudflight.integra.backend.utils.enums.Difficulty;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private Long cityId;

    public Quest(Long id, String title, String description, Difficulty difficulty, int exp, Long cityId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.exp = exp;
        this.cityId = cityId;
    }

    public Quest() {}
}
