package cloudflight.integra.backend.quests.model;

public class Quest {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;

    public enum Difficulty {
        Easy, Medium, Hard
    }

    private int exp;
    private Long cityId;

    public Quest (Long id, String title, String description, Difficulty difficulty, int exp, Long cityId){
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.exp = exp;
        this.cityId = cityId;
    }

    public Quest(){}

    public Long getId() { return id; }
    public void setId(Long newId) { this.id = newId; }
    public String getTitle() { return title; }
    public void setTitle(String newTitle) { this.title = newTitle; }
    public String getDescription() { return description; }
    public void setDescription(String newDescription) { this.description = newDescription; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty newDifficulty) {this.difficulty = newDifficulty; }
    public int getExp() { return exp; }
    public void setExp(int newExp) { this.exp = newExp; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long newCityId) { this.cityId = newCityId; } 
}
