package comstucom.movip88.game.characters;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

import comstucom.movip88.App;
import comstucom.movip88.R;
import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.Scene;
import comstucom.movip88.engine.SpriteSequence;
import comstucom.movip88.engine.TiledScene;
import comstucom.movip88.game.ScenePregunta;

// The main character (player)
@SuppressWarnings("unused")
public class Bonk extends GameObject {
    // Bonk specific attributes
    private int vx;         // vel-X is 1 or 2 (boosted velocity)
    private int vy;
    private boolean isJumping;
    private int score;
    private int lives;

    private boolean boosterExtraSalto = false;

    private Game game;

    // Useful constants
    private static final int MAX_VELOCITY = 4;
    private static final int JUMP_VELOCITY = -8;

    private static final int JUMP_VELOCITY_BOOSTER = -20;


    private static final int PAD_LEFT = 2;
    private static final int PAD_TOP = 0;
    private static final int COL_WIDTH = 20;
    private static final int COL_HEIGHT = 32;

    public final static int STATE_STANDING_FRONT = 0;
    public final static int STATE_WALKING_LEFT = 1;
    public final static int STATE_WALKING_RIGHT = 2;
    public final static int STATE_DEAD = 3;
    public final static int STATE_JUMPING_LEFT = 4;
    public final static int STATE_JUMPING_RIGHT = 5;
    public final static int STATE_FALLING_LEFT = 6;
    public final static int STATE_FALLING_RIGHT = 7;
    public final static int STATE_JUMPING_FRONT = 8;
    public final static int STATE_FALLING_BOOSTER = 9;

    // State change matrix depending on movement direction
    private static final int[] NEW_STATES = {
        STATE_JUMPING_LEFT, STATE_JUMPING_FRONT, STATE_JUMPING_RIGHT,
        STATE_WALKING_LEFT, STATE_STANDING_FRONT, STATE_WALKING_RIGHT,
        STATE_FALLING_LEFT, STATE_JUMPING_FRONT, STATE_FALLING_RIGHT
    };

    // Constructor
    public Bonk(Game game, int x, int y, int lives) {
        super(game, x, y);
        this.lives = lives;
        this.game = game;
        this.reset(x, y);
        this.addTag("bonk");
        for (int i = 0; i < 9; i++) {
            this.addSpriteSequence(i, i); // The first 0-8 states are indexed animations 0-8
        }
        this.addSpriteSequence(9, 13);
    }

    //lives
    public int getLives() { return lives; }
    public void restarVida() { lives--; }

    // Score related
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int score) { this.score += score; }

    //salto booster
    public void aTocadoBoosterSalto() {
        this.boosterExtraSalto = true;
        final Handler h = new Handler();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                boosterExtraSalto = false;
            }
        };

        h.postDelayed(myRunnable, 10000);

    }
    public boolean isBoosterExtraSalto() { return boosterExtraSalto; }

    // Reset Bonk to a known position
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = 2;
    }

    // Change state method
    private void changeState(int state) {
        if (this.state == state) return;
        this.state = state;
        SpriteSequence spriteSequence = getCurrentSpriteSequence();
        spriteSequence.reset();
    }

    // Dying is exactly state 3
    public boolean isDead() { return (state == STATE_DEAD); }

    // And kill him is exactly change its state to 3
    public void die() {
        changeState(STATE_DEAD);
        final Handler h = new Handler();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                ScenePregunta scene = new ScenePregunta(game, App.getContext().getString(R.string.pregunta),  App.getContext().getString(R.string.afirmacion), App.getContext().getString(R.string.negacion), score);
                game.loadScene(scene);
            }
        };

        h.postDelayed(myRunnable, 2000);
    }

    // User input helper methods
    private boolean left, right, jump;
    public void goLeft() { left = true; right = false; }
    public void goRight() { left = false; right = true; }
    public void stopLR() { left = right = false; }
    public void jump() { jump = true; }
    private void clearJump() { jump = false; }
    private boolean isLeft() { return left; }
    private boolean isRight() { return right; }
    private boolean isJump() { return jump; }

    @Override public void physics(long deltaTime) {
        // If died, no physics
        if (state == STATE_DEAD) return;

        // Analyze user input
        int vx = 0;
        if (this.isLeft()) vx = -this.vx;
        else if (this.isRight()) vx = this.vx;
        if (this.isJump()) {
            if (!isJumping) {       // Avoid double jumps
                vy = boosterExtraSalto ? JUMP_VELOCITY_BOOSTER : JUMP_VELOCITY;
                isJumping = true;
            }
            this.clearJump();
        }

        // Apply physics and tests to scene walls and grounds (only if it's in a TiledScene scene)
        Scene scene = game.getScene();
        if (!(scene instanceof TiledScene)) return;
        TiledScene tiledScene = (TiledScene) scene;
        // 1) detect wall to right
        int newX = x + vx;
        int newY = y;
        if (vx > 0) {
            int col = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = col * 16 - PAD_LEFT - COL_WIDTH - 1;
                    break;
                }
            }
        }
        // 2) detect wall to left
        if (vx < 0) {
            int col = (newX + PAD_LEFT) / 16;
            int r1 = (newY + PAD_TOP) / 16;
            int r2 = (newY + PAD_TOP + COL_HEIGHT - 1) / 16;
            for (int row = r1; row <= r2; row++) {
                if (tiledScene.isWall(row, col)) {
                    newX = (col + 1) * 16 - PAD_LEFT;
                    break;
                }
            }
        }
        // 3) detect ground
        // physics (try fall and detect ground)
        vy++; if (vy > MAX_VELOCITY) vy = MAX_VELOCITY;
        newY = y + vy;
        if (vy >= 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP + COL_HEIGHT) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isGround(row, col)) {
                    newY = row * 16 - PAD_TOP - COL_HEIGHT;
                    vy = 0;
                    isJumping = false;
                    break;
                }
            }
        }
        // 4) detect ceiling
        if (vy < 0) {
            int c1 = (newX + PAD_LEFT) / 16;
            int c2 = (newX + PAD_LEFT + COL_WIDTH) / 16;
            int row = (newY + PAD_TOP) / 16;
            for (int col = c1; col <= c2; col++) {
                if (tiledScene.isWall(row, col)) {
                    newY = (row + 1) * 16 - PAD_TOP;
                    vy = 0;
                    break;
                }
            }
        }

        // Apply resulting physics
        x = newX;
        y = newY;

        // Apply screen limits
        x = Math.max(x, -PAD_LEFT);
        x = Math.min(x, tiledScene.getSceneFullWidth() - COL_WIDTH);
        y = Math.min(y, tiledScene.getSceneFullHeight() - COL_HEIGHT);

        // Decide the out state
        int c = (vx < 0) ? 0 : ((vx == 0) ? 1 : 2);
        int r = (vy < 0) ? 0 : ((vy == 0) ? 1 : 2);

        int newState = NEW_STATES[r * 3 + c];

        changeState(isBoosterExtraSalto() && newState == STATE_JUMPING_FRONT && vy > 0 ? STATE_FALLING_BOOSTER : newState);
    }

    // The collision rect is only valid while alive
    @Override public Rect getCollisionRect() {
        return (state == STATE_DEAD) ? null : collisionRect;
    }

    // Updates the collision rect around the character
    @Override public void updateCollisionRect() {
        collisionRect.set(
                x + PAD_LEFT,
                y + PAD_TOP,
                x + PAD_LEFT + COL_WIDTH,
                y + PAD_TOP + COL_HEIGHT
        );
    }
}
