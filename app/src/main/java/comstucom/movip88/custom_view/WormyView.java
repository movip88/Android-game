package comstucom.movip88.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import comstucom.movip88.R;

public class WormyView extends View {
    public static final int SLOW_DOWN = 5;
    public static int TILE_SIZE = 48;
    private int nCols, nRows, top, left, bottom, right;
    private int wormX, wormY, score = 0, numCoins = 0, slowdown, lives;
    private boolean playing = false, pause = false;
    private char map[];
    private Paint paint, paintText;
    private Bitmap tiles, wormLeft, wormRight, worm, heard, deadWorm;

    public WormyView(Context context) { this(context, null, 0); }
    public WormyView(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public WormyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3.0f);
        tiles = BitmapFactory.decodeResource(getResources(), R.drawable.tiles);
        wormLeft = BitmapFactory.decodeResource(getResources(), R.drawable.worm_left);
        wormRight = BitmapFactory.decodeResource(getResources(), R.drawable.worm_right);
        heard = BitmapFactory.decodeResource(getResources(), R.drawable.heard);
        deadWorm = BitmapFactory.decodeResource(getResources(), R.drawable.worm_dead);
        worm = wormLeft;
        paintText = new Paint();
        paintText.setColor(Color.GREEN);
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override public void onMeasure(int specW, int specH) {
        int w = MeasureSpec.getSize(specW);
        int h = MeasureSpec.getSize(specH);
        if ((w != 0) && (h != 0)) {
            TILE_SIZE = 64;
            if ((w < 640) || (h < 640)) TILE_SIZE = 48;
            if ((w < 480) || (h < 480)) TILE_SIZE = 32;
            if ((w < 320) || (h < 320)) TILE_SIZE = 16;
            nCols = w / TILE_SIZE;
            nRows = h / TILE_SIZE;
            left = ( w - nCols * TILE_SIZE) / 2;
            top = (h - nRows * TILE_SIZE) / 2;
            right = left + nCols * TILE_SIZE;
            bottom = top + nRows * TILE_SIZE;
            if (!playing) resetMap(false);
            paintText.setTextSize(TILE_SIZE);
        }
        setMeasuredDimension(w, h);
    }

    public void newGame() {
        slowdown = SLOW_DOWN;
        score = 0;
        lives = 3;
        worm = wormLeft;
        playing = true;
        resetMap(true);
    }

    public void resetMap(boolean full) {
        if ((nCols <= 0) || (nRows <= 0)) return;
        wormX = nCols / 2;
        wormY = nRows / 2;
        int size = nCols * nRows;
        map = new char[size];
        // Empty the board
        for (int i = 0; i < size; i++) map[i] = ' ';
        // Border around
        for (int i = 0; i < nCols; i++) {
            map[i] = 'X';
            map[(nRows - 1) * nCols + i] = 'X';
        }
        for (int i = 0; i < nRows; i++) {
            map[i * nCols] = 'X';
            map[(i + 1) * nCols - 1] = 'X';
        }
        if (full) {
            // Protect worm position
            map[wormY * nCols + wormX] = 'W';
            // Random plants
            int placed = 0;
            while (placed < size / 20) {
                int i = (int) (Math.random() * nCols);
                int j = (int) (Math.random() * nRows);
                int idx = j * nCols + i;
                if (map[idx] == ' ') {
                    map[idx] = (char) ('P' + (int) (Math.random() * 4));
                    placed++;
                }
            }
            // Random coins
            placed = 0;
            numCoins = size / 50;
            while (placed < numCoins) {
                int i = (int) (Math.random() * nCols);
                int j = (int) (Math.random() * nRows);
                int idx = j * nCols + i;
                if (map[idx] == ' ') {
                    map[idx] = (char) ('C' + (int) (Math.random() * 5));
                    placed++;
                }
            }
            // Remove worm lock
            map[wormY * nCols + wormX] = ' ';
        }
        this.invalidate();
    }

    private Rect src = new Rect(0, 0, 0, 0);
    private Rect dst = new Rect(0, 0, 0, 0);
    public void drawTile(Canvas canvas, int i, int x, int y) {
        int cols = tiles.getWidth() / 16;
        int row = i / cols;
        int col = i % cols;
        src.left = col * 16;
        src.top = row * 16;
        src.right = src.left + 16;
        src.bottom = src.top + 16;
        dst.left = x;
        dst.top = y;
        dst.right = dst.left + TILE_SIZE;
        dst.bottom = dst.top + TILE_SIZE;
        canvas.drawBitmap(tiles, src, dst, paint);
    }
    public void drawWorm(Canvas canvas, int x, int y) {
        src.left = 0;
        src.top = 0;
        src.right = 32;
        src.bottom = 32;
        dst.left = x;
        dst.top = y;
        dst.right = dst.left + TILE_SIZE;
        dst.bottom = dst.top + TILE_SIZE;
        canvas.drawBitmap(worm, src, dst, paint);
    }

    public void drawLives(Canvas canvas, int x, int y) {
        int posFirstHeard = (x / 2) - (60 * lives) / 2;

        for(int i = 0; i < lives; i++){
            src.left = 0;
            src.top = 0;
            src.right = 30;
            src.bottom = 30;
            dst.left = posFirstHeard + (60 * i);
            dst.top = y - 70;
            dst.right = dst.left + TILE_SIZE;
            dst.bottom = dst.top + TILE_SIZE;
            canvas.drawBitmap(heard, src, dst, paint);
        }
    }

    @Override public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        if (map == null) return;
        int idx = 0, x, y = top;
        for (int i = 0; i < nRows; i++) {
            x = left;
            for (int j = 0; j < nCols; j++) {
                int s = 3;
                char m = map[idx];
                switch (m) {
                    case 'X': s = 28; break;
                    case 'C': s = 16; map[idx] = 'D'; break;
                    case 'D': s = 17; map[idx] = 'E'; break;
                    case 'E': s = 18; map[idx] = 'F'; break;
                    case 'F': s = 19; map[idx] = 'G'; break;
                    case 'G': s = 23; map[idx] = 'C'; break;
                    case 'P': s = 2; break;
                    case 'Q': s = 21; break;
                    case 'R': s = 25; break;
                    case 'S': s = 29; break;
                }
                idx++;
                drawTile(canvas, s, x, y);
                x += TILE_SIZE;
            }
            y += TILE_SIZE;
        }
        drawWorm(canvas, left + wormX * TILE_SIZE, top + wormY * TILE_SIZE);
        canvas.drawRect(left, top, right, bottom, paint);

        drawLives(canvas , getWidth() , getHeight());

        String text = "Puntuacion -> " + score;
        canvas.drawText(text, (getWidth() / 2) - paintText.measureText(text) / 2, 50, paintText);
    }

    private int counter = 0;
    public void update(float accelerationX, float accelerationY) {
        if (!playing) return;
        if (pause) return;
        if (++counter == slowdown) {
            counter = 0;
            int newX = wormX, newY = wormY;
            if (accelerationX < -2) { newX--; worm = wormLeft; }
            if (accelerationX > +2) { newX++; worm = wormRight; }
            if (accelerationY < -2) newY--;
            if (accelerationY > +2) newY++;
            int idx = newY * nCols + newX;
            if (map[idx] != 'X') {
                wormX = newX;
                wormY = newY;
                if ((map[idx] >= 'C') && (map[idx] <= 'G')) {
                    // Coin collected!
                    score += 10;
                    map[idx] = ' ';
                    numCoins--;
                    if (numCoins == 0) {
                        slowdown--;
                        if (slowdown < 1) slowdown = 1;
                        resetMap(true);
                    }
                    if (listener != null) listener.scoreUpdated(this, score);
                }
                else if ((map[idx] >= 'P') && (map[idx] <= 'S')) {
                    // Plant touched!
                    if(lives == 0){
                        map[idx] = ' ';
                        worm = deadWorm;
                        playing = false;
                        if (listener != null) listener.gameLost(this, this.score);
                    }else{
                        lives --;
                        map[idx] = ' ';
                        if (listener != null) listener.livesDown(this);
                    }
                }
            }
        }
        this.invalidate();
    }

    public void pauseGame() {
        this.pause = true;
    }

    public void reanudeGame() {
        this.pause = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public interface WormyListener {
        void scoreUpdated(View view, int score);
        void livesDown(View view);
        void gameLost(View view, int score);
    }

    private WormyListener listener;
    public void setWormyListener(WormyListener listener) {
        this.listener = listener;
    }
}
