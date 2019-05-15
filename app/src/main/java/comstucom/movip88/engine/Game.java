package comstucom.movip88.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import comstucom.movip88.R;
import comstucom.movip88.game.Scene01;
import comstucom.movip88.game.ScenePregunta;
import comstucom.movip88.game.characters.Bonk;

// This class is the base for a generic game
@SuppressWarnings({"unused", "SameParameterValue"})
public class Game {

    private GameEngine gameEngine;
    private Scene scene;                // The current scene
    private boolean paused = true;      // True if the game is paused

    private Integer level;

    private Bonk bonk;

    private int[] scenesResources = {R.raw.scene2, R.raw.mini, R.raw.scene};

    // Constructor (bidirectional relationship)
    public Game(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        gameEngine.setGame(this);
        this.level = 0;
    }

    // Useful getters and helpers
    public GameEngine getGameEngine() { return gameEngine; }
    public Audio getAudio() { return gameEngine.getAudio(); }
    public Scene getScene() { return scene; }
    public BitmapSet getBitmapSet() { return gameEngine.getBitmapSet(); }
    public Bitmap getBitmap(int index) { return getBitmapSet().getBitmap(index); }
    public SpriteSequence getSpriteSequence(int index) { return getBitmapSet().getSpriteSequence(index); }
    public int getScreenWidth() { return gameEngine.getScreenWidth(); }
    public int getScreenHeight() { return gameEngine.getScreenHeight(); }
    public boolean isPaused() { return paused; }

    public void setBonk(Bonk bonk) {
        this.bonk = bonk;
    }

    public Bonk getBonk() {
        return bonk;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) { this.level = level; }

    public int currentSceneResource(){
        return scenesResources[this.level];
    }

    // Methods to be called by the game engine on start, stop, resume and pause
    public void start() { }
    public void stop() { }
    public void resume() { paused = false; }
    public void pause() { paused = true; }

    public void pasNextLevel(){
        level++;
        if(level < scenesResources.length){
            bonk.reset(0,0);
            Scene01 scene = new Scene01(this);
            loadScene(scene);
        }else{
            ScenePregunta scene = new ScenePregunta(this,"Felicitats, quieres volver a jugar ?", "Si","No", bonk.getScore());
            loadScene(scene);
        }

    }

    public void resetValues(){
        bonk = null;
        level = 0;
    }

    // Sets the current scene
    public void loadScene(Scene scene) { this.scene = scene; }

    // Process input from user
    void processInput() {
        if (scene == null) return;
        scene.processInput();
    }

    // The physics cycle (if not paused)
    void physics(long deltaTime) {
        if (scene == null) return;
        if (paused) return;
        scene.physics(deltaTime);
    }

    // The drawing cycle
    void draw(Canvas canvas) {
        if (scene == null) return;
        scene.draw(canvas);
    }

}
