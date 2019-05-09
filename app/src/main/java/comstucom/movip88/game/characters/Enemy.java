package comstucom.movip88.game.characters;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;

// This class only serves for tagging as "enemy" a collection of GameObjects
abstract public class Enemy extends GameObject {

    // Constructor
    public Enemy(Game game, int x, int y) {
        super(game, x, y);
        this.addTag("enemy");
    }

}
