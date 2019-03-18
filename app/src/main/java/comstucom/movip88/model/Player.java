package comstucom.movip88.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Clase serializable para guardar la información de un usuario
 */
public class Player implements Comparable<Player> {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String playerName;

    @SerializedName("image")
    private String profilePicture;

    @SerializedName("totalScore")
    private int totalScore;

    @SerializedName("lastLevel")
    private int lastLevel;

    @SerializedName("lastScore")
    private int lastScore;

    @SerializedName("scores")
    private List<Score> historial;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getProfilePicture() {
        if (profilePicture == null){
            profilePicture = "https://api.flx.cat/imgs/unknown.png";
        }
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public List<Score> getHistorial() {
        return historial;
    }

    public void setHistorial(List<Score> historial) {
        this.historial = historial;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getLastLevel() {
        return lastLevel;
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public int compareTo(Player p) {
        return p.getTotalScore().compareTo(this.getTotalScore());
        }
        }
