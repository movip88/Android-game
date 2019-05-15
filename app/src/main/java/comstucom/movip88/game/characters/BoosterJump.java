package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.SpriteSequence;

public class BoosterJump extends GameObject {

    // Constructor
    public BoosterJump(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("boosterJump");
        this.addSpriteSequence(0, 12);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 16, y + 16);
    }

}
