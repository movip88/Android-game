package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.SpriteSequence;

public class Teleport extends GameObject {

    private int newX;
    private int newY;

    // Constructor
    public Teleport(Game game, int x, int y, int newX, int newY) {
        super(game, x, y);
        this.addTag("teleport");
        this.addSpriteSequence(0, 14);
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.randomizeSprite();
        this.newX = newX;
        this.newY = newY;
    }

    public int getNewX() {
        return newX;
    }

    public int getNewY() {
        return newY;
    }

    // A coin doesn't move
    @Override public void physics(long deltaTime) { }

    // The collision rect around the coin
    @Override public void updateCollisionRect() {
        collisionRect.set(x, y, x + 35, y + 32);
    }

}
