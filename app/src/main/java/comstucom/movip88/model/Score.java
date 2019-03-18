package comstucom.movip88.model;

import com.google.gson.annotations.SerializedName;

/**
 * Clase serializable para guardar la información de un usuario
 */
public class Score implements Comparable<Score>{
    @SerializedName("level")
    private Integer level;

    @SerializedName("score")
    private Integer score;

    @SerializedName("playedAt")
    private String timePLayed;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTimePLayed() {
        return timePLayed;
    }

    public void setTimePLayed(String timePLayed) {
        this.timePLayed = timePLayed;
    }

    @Override
    public int compareTo(Score o) {
        return this.level.compareTo(o.getLevel()) == 0 ? o.getScore().compareTo(this.score) : this.level.compareTo(o.getLevel());
    }
}
