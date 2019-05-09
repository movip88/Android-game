package comstucom.movip88.game.characters;

import android.util.Log;

import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.Scene;
import comstucom.movip88.engine.SpriteSequence;
import comstucom.movip88.engine.TiledScene;

public class IntelligentCrab extends Enemy{

    private static final int PAD_LEFT = 2;
    private static final int PAD_TOP = 0;
    private static final int COL_WIDTH = 20;
    private static final int COL_HEIGHT = 32;

    // Constructor
    public IntelligentCrab(Game game, int x, int y) {
        super(game, x, y - 5);
        this.addTag("intelligentCrab");
        this.addSpriteSequence(0, 10);
    }

    // The crab moves horizontally between x0 and x1
    @Override public void physics(long deltaTime) {
        Scene scene = game.getScene();
        if (!(scene instanceof TiledScene)) return;
        TiledScene tiledScene = (TiledScene) scene;

        int newX =  this.x;
        if(game.getBonk() != null){
            if(game.getBonk().getX() > newX) {
                newX += 1;
                int col = (newX + PAD_LEFT + COL_WIDTH + 8) / 16;
                int r1 = (y + PAD_TOP) / 16;
                int r2 = (y + PAD_TOP + COL_HEIGHT - 1) / 16;
                for (int row = r1; row <= r2; row++) {
                    if (tiledScene.isWall(row, col)) {
                        newX = this.x;
                        break;
                    }
                }
            }else if(game.getBonk().getX() < newX) {
                newX -= 1;
                int col = (newX + PAD_LEFT) / 16;
                int r1 = (y + PAD_TOP) / 16;
                int r2 = (y + PAD_TOP + COL_HEIGHT - 1) / 16;
                for (int row = r1; row <= r2; row++) {
                    if (tiledScene.isWall(row, col)) {
                        newX = (col + 1) * 16 - PAD_LEFT;
                        break;
                    }
                }
            }
        }

        this.x = newX;
    }

    // The collision rect around the crab will consider the pincers' position
    @Override public void updateCollisionRect() {
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        int currentSpriteIndex = spriteSequence.getCurrentSpriteIndex();
        int top = y + 8 - ((currentSpriteIndex < 6) ? 8 : 0);
        int bottom = y + 22 + ((currentSpriteIndex >= 6) ? 8 : 0);
        collisionRect.set(x, top, x + 32, bottom);
    }
}
