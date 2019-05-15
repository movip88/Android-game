package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.SpriteSequence;

public class Teleport extends GameObject {
    // Constructor
    public Teleport(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("teleport");
        this.addSpriteSequence(0, 14);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 36, y + 33);
    }

}
