package comstucom.movip88.model;

import java.io.Serializable;

public class Partida implements Serializable {

    private int lives;
    private int score;
    private int level;

    public Partida() {
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}