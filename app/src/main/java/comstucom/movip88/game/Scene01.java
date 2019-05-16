package comstucom.movip88.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Locale;

import comstucom.movip88.App;
import comstucom.movip88.R;
import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameEngine;
import comstucom.movip88.engine.GameObject;
import comstucom.movip88.engine.OnContactListener;
import comstucom.movip88.engine.TiledScene;
import comstucom.movip88.engine.Touch;
import comstucom.movip88.game.characters.Bonk;
import comstucom.movip88.game.characters.BoosterJump;
import comstucom.movip88.game.characters.Coin;
import comstucom.movip88.game.characters.Crab;
import comstucom.movip88.game.characters.ExitElement;
import comstucom.movip88.game.characters.IntelligentCrab;
import comstucom.movip88.game.characters.Teleport;

// A fully playable tiled scene
public class Scene01 extends TiledScene implements OnContactListener {

    // We keep a specific reference to the player
    private Bonk bonk;
    // Used for specific painting
    private Paint paintKeySymbol, paintKeyBackground, paintScore, triangle, paintBooster;

    private Bitmap heard;

    private Rect src = new Rect(0, 0, 0, 0);
    private Rect dst = new Rect(0, 0, 0, 0);

    // Constructor
    public Scene01(Game game) {
        super(game);
        // Load the bitmap set for this game
        GameEngine gameEngine = game.getGameEngine();

        heard = BitmapFactory.decodeResource(gameEngine.getResources(), R.drawable.heard);

        // Create the main character (player)
        bonk = game.getBonk() != null ? game.getBonk() : new Bonk(game, 0, 0, 3);

        this.add(bonk);
        game.setBonk(bonk);
        // Set the follow camera to the player
        this.setCamera(bonk);
        // The screen will hold 16 rows of tiles (16px height each)
        this.setScaledHeight(16 * 16);
        // Pre-loading of sound effects
        game.getAudio().loadSoundFX(new int[]{ R.raw.coin, R.raw.die, R.raw.pause, R.raw.golpe } );
        // Load the scene tiles from resource
        this.loadFromFile(game.currentSceneResource());
        // Add contact listeners by tag names
        this.addContactListener("bonk", "enemy", this);
        this.addContactListener("bonk", "coin", this);
        this.addContactListener("bonk", "door", this);
        this.addContactListener("bonk", "boosterJump", this);
        this.addContactListener("bonk", "teleport", this);

        // Prepare the painters for drawing
        paintKeyBackground = new Paint();
        paintKeyBackground.setColor(Color.argb(20, 0, 0, 0));
        paintKeySymbol = new Paint();
        paintKeySymbol.setColor(Color.GRAY);
        paintKeySymbol.setTextSize(10);
        paintScore = new Paint(paintKeySymbol);
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.dseg);
        paintScore.setTypeface(typeface);
        paintScore.setColor(Color.WHITE);

        paintBooster = new Paint();
        paintBooster.setColor(Color.GREEN);
        paintBooster.setTextSize(15);

        triangle = new Paint();
        triangle.setStrokeWidth(4);
        triangle.setColor(Color.WHITE);
        triangle.setStyle(Paint.Style.FILL_AND_STROKE);
        triangle.setAntiAlias(true);
    }

    // Overrides the base parser adding specific syntax for coins and crabs
    @Override
    protected GameObject parseLine(String cmd, String args) {
        // Lines beginning with "COIN"
        if (cmd.equals("COIN")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new Coin(game, coinX, coinY);
        }

        // Lines beginning with "BOX"
        if (cmd.equals("DOOR")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new ExitElement(game, coinX, coinY);
        }

        // Lines beginning with "JUMP_BOOSTER"
        if (cmd.equals("JUMP_BOOSTER")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;
            return new BoosterJump(game, coinX, coinY);
        }
        // Lines beginning with "TELEPORT"
        if (cmd.equals("TELEPORT")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 4) return null;
            int coinX = Integer.parseInt(parts2[0].trim()) * 16;
            int coinY = Integer.parseInt(parts2[1].trim()) * 16;

            int newX = Integer.parseInt(parts2[2].trim()) * 16;
            int newY = Integer.parseInt(parts2[3].trim()) * 16;

            return new Teleport(game, coinX, coinY, newX, newY);
        }

        // Lines beginning with "CRAB"
        if (cmd.equals("CRAB")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 3) return null;
            int crabX0 = Integer.parseInt(parts2[0].trim()) * 16;
            int crabX1 = Integer.parseInt(parts2[1].trim()) * 16;
            int crabY = Integer.parseInt(parts2[2].trim()) * 16;
            return new Crab(game, crabX0, crabX1, crabY);
        }

        // Lines beginning with "CRAB"
        if (cmd.equals("INTELLIGENT_CRAB")) {
            String[] parts2 = args.split(",");
            if (parts2.length != 2) return null;
            int crabX = Integer.parseInt(parts2[0].trim()) * 16;
            int crabY = Integer.parseInt(parts2[1].trim()) * 16;
            return new IntelligentCrab(game, crabX, crabY);
        }
        // Test the common basic parser
        return super.parseLine(cmd, args);
    }

    // User input processing
    @Override
    public void processInput() {
        // Iterate over all the queued touch events
        Touch touch;
        while ((touch = game.getGameEngine().consumeTouch()) != null) {
            if (getScreenWidth()*getScreenHeight() == 0) continue;
            // Convert the X,Y to percentages of screen
            int x = touch.getX() * 100 / getScreenWidth();
            int y = touch.getY() * 100 / getScreenHeight();
            // Bottom-left corner (left-right)
            if ((y > 75) && (x < 40)) {
                if (!touch.isTouching()) bonk.stopLR();     // STOP
                else if (x < 20) bonk.goLeft();             // LEFT
                else bonk.goRight();                        // RIGHT
            }
            // Bottom-right corner (jump)
            else if ((y > 75) && (x > 80) ) {               // JUMP
                if (touch.isDown()) bonk.jump();
            }

            // Exit game
            else if ((y > 5 && y < 20) && (x < 55 && x > 45) && game.isPaused()) { game.getGameEngine().finishGame(); }

            // Rest of screen (pause)
            else if (touch.isDown()) {                      // TOGGLE PAUSE
                if (game.isPaused()) game.resume();
                else game.pause();
            }
        }

        // Process the computer's keyboard if the game is run inside an emulator
        int keycode;
        while ((keycode = game.getGameEngine().consumeKeyTouch()) != KeyEvent.KEYCODE_UNKNOWN) {
            switch (keycode) {
                case KeyEvent.KEYCODE_Z:                    // LEFT
                    bonk.goLeft();
                    break;
                case KeyEvent.KEYCODE_X:                    // RIGHT
                    bonk.goRight();
                    break;
                case KeyEvent.KEYCODE_M:                    // JUMP
                    bonk.jump();
                    break;
                case KeyEvent.KEYCODE_P:                    // TOGGLE PAUSE
                    if (game.isPaused()) game.resume();
                    else game.pause();
                    break;
            }
        }
    }

    // Contact detection listener: A contact has been detected and must be processed
    // The object1 (based on tag1) overlapped with object2 (based on tag2)
    @Override
    public void onContact(String tag1, GameObject object1, String tag2, GameObject object2) {
        Log.d("flx", "Contact between a " + tag1 + " and " + tag2);
        // Contact between Bonk and a coin
        if (tag2.equals("coin")) {
            this.getGame().getAudio().playSoundFX(0);
            object2.removeFromScene();
            bonk.addScore(10);
        }
        // Contact between Bonk and an enemy
        else if (tag2.equals("enemy")) {
            if(bonk.getState() == Bonk.STATE_FALLING_BOOSTER){
                //TODO poner sonido
                object2.removeFromScene();
                bonk.addScore(100);
            }else{
                if(bonk.getLives() > 0){
                    this.getGame().getAudio().playSoundFX(3);
                    bonk.restarVida();
                    bonk.reset(0,0);
                }else{
                    this.getGame().getAudio().playSoundFX(1);
                    object2.removeFromScene();
                    bonk.die();
                }
            }
        }
        //contact exit element
        else if(tag2.equals("door")){
            game.setBonk(bonk);
            game.pasNextLevel();
        }

        //contact jump booster element
        else if(tag2.equals("boosterJump")){
            //TODO poner sonido
            bonk.aTocadoBoosterSalto();
            object2.removeFromScene();
        }

        //contact teleport element
        else if(tag2.equals("teleport")){
            //TODO poner sonido
            bonk.reset(((Teleport)object2).getNewX(),((Teleport)object2).getNewY());
        }
    }

    // Overrides the basic draw by adding the translucent keyboard and the score
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Translucent keyboard on top
        canvas.save();
        canvas.scale(getScale() * getScaledWidth() / 100, getScale() * getScaledHeight() / 100);
        canvas.drawRect(1, 76, 19, 99, paintKeyBackground);
        canvas.drawText("«", 8, 92, paintKeySymbol);
        canvas.drawRect(21, 76, 39, 99, paintKeyBackground);
        canvas.drawText("»", 28, 92, paintKeySymbol);
        canvas.drawRect(81, 76, 99, 99, paintKeyBackground);
        canvas.drawText("^", 88, 92, paintKeySymbol);

        if(game.isPaused()){
            canvas.drawRect(45, 5, 55, 20, paintKeyBackground);
            canvas.drawText("X", 47, 16, paintKeySymbol);
        }

        canvas.restore();

        // Score on top-right corner
        canvas.scale(getScale(), getScale());
        paintScore.setTextSize(10);
        String score = String.format(Locale.getDefault(), "%06d", bonk.getScore());
        canvas.drawText(score, getScaledWidth() - 50, 10, paintScore);

        //booster active
        if(bonk.isBoosterExtraSalto()){

            String texto = App.getContext().getString(R.string.berserker);
            canvas.drawText(texto, (getScaledWidth() / 2) - paintBooster.measureText(texto) / 2, 20, paintBooster);
        }

        //draw lives
        int posFirstHeard = getScaledWidth() - 20;

        for(int i = 0; i < bonk.getLives(); i++){
            src.left = 0;
            src.top = 0;
            src.right = 30;
            src.bottom = 30;
            dst.left = posFirstHeard - (25 * i);
            dst.top = 20;
            dst.right = dst.left + 20;
            dst.bottom = dst.top + 20;
            canvas.drawBitmap(heard, src, dst, paintScore);
        }

        if(game.isPaused()){
            int rx = 200;
            int ry = 60;

            Point a = new Point(rx, ry);
            Point b = new Point(rx, ry+100);
            Point c = new Point(rx+87, ry+50);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.lineTo(a.x, a.y);
            path.close();

            canvas.drawPath(path, triangle);
        }
    }
}
