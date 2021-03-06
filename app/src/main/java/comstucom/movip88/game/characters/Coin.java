package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.SpriteSequence;

// A coin to be collected by the player
public class Coin extends GameObject {

    // Constructor
    public Coin(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("coin");
        this.addSpriteSequence(0, 9);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 12, y + 12);
    }
}
