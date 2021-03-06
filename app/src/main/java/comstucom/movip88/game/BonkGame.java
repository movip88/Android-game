package comstucom.movip88.game;

import comstucom.movip88.App;
import comstucom.movip88.R;
import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameEngine;

// This game is a Game instance
public class BonkGame extends Game {

    // Constructor
    BonkGame(GameEngine gameEngine) {
        super(gameEngine);
    }

    // Method to be called when the game is first started
    @Override
    public void start() {
        // When the game is loaded, the Scene01 is presented to the user
        //Scene01 scene = new Scene01(this);
        ScenePregunta scene = new ScenePregunta(this, App.getContext().getString(R.string.iniciarJuego), App.getContext().getString(R.string.afirmacion),App.getContext().getString(R.string.negacion));
        this.loadScene(scene);
        // Background music
        getAudio().loadMusic(R.raw.music);
    }

    // Method to be called when the game is being closed
    @Override
    public void stop() {
        // Nothing special for now
    }

    // Method to be called when the game returns from pause
    @Override
    public void resume() {
        super.resume();
        getAudio().startMusic();
    }

    // Method to be called when the game goes to pause
    @Override
    public void pause() {
        super.pause();
        getAudio().stopMusic();
    }
}
