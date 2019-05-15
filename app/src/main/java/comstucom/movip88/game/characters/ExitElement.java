package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.SpriteSequence;

public class ExitElement extends GameObject {

    // Constructor
    public ExitElement(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("door");
        this.addSpriteSequence(0, 11);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 37, y + 40);
    }

}
