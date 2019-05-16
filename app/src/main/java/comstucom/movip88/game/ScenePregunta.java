package comstucom.movip88.game;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import comstucom.movip88.APIResponse;
import comstucom.movip88.App;
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.engine.Game;
import comstucom.movip88.engine.GameEngine;
import comstucom.movip88.engine.Scene;
import comstucom.movip88.engine.Touch;
import comstucom.movip88.exception.ExceptionTokenNull;
import comstucom.movip88.game.characters.Bonk;
import comstucom.movip88.model.Partida;

public class ScenePregunta extends Scene {

    private Paint paintKeySymbol, paintKeyBackground, paintQuestion, paintScore;
    private Game game;
    private String pregunta, aceptar, rechazar;
    private Integer puntuacion;

    public ScenePregunta(Game game, String pregunta, String aceptar, String rechazar) {
        this(game, pregunta, aceptar, rechazar, null);
    }

    public ScenePregunta(Game game, String pregunta, String aceptar, String rechazar, Integer puntuacion) {
        super(game);

        this.game = game;
        this.pregunta = pregunta;
        this.aceptar = aceptar;
        this.rechazar = rechazar;

        GameEngine gameEngine = game.getGameEngine();
        if(gameEngine.getBitmapSet() == null) gameEngine.loadBitmapSet(R.raw.sprites, R.raw.sprites_info, R.raw.sprites_seq);

        this.puntuacion = puntuacion;

        paintKeyBackground = new Paint();
        paintKeyBackground.setColor(Color.argb(20, 0, 0, 0));
        paintKeySymbol = new Paint();
        paintKeySymbol.setColor(Color.GRAY);
        paintKeySymbol.setTextSize(50);

        paintQuestion = new Paint(paintKeySymbol);
        paintQuestion.setColor(Color.WHITE);
        paintQuestion.setTextSize(70);

        paintScore = new Paint(paintKeySymbol);
        paintScore.setColor(Color.WHITE);
        paintScore.setTextSize(40);

        if(puntuacion != null){
            subirScore(String.valueOf(game.getLevel()), String.valueOf(puntuacion));
            HelperUser.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).eliminarPartidaGuardada();
            game.resetValues();
        }else{
            Partida partida = HelperUser.getInstance(((AppCompatActivity) game.getGameEngine().getContext())).cogerPartida();
            if(partida != null){
                game.setLevel(partida.getLevel());
                Bonk bonk = new Bonk(game, 0, 0, partida.getLives());
                bonk.setScore(partida.getScore());
                game.setBonk(bonk);
            }
        }
    }

    final static String URL_REGISTER_VER = "https://api.flx.cat/dam2game/user/score";

    public void subirScore(final String level, final String score) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER_VER,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                            HelperUser.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).errorToken(((AppCompatActivity)game.getGameEngine().getContext()));
                        }else if(apiResponse.getErrorCode() == 0){
                            Toast toast = Toast.makeText(((AppCompatActivity)game.getGameEngine().getContext()).getApplicationContext(), R.string.setDatosCorrectamente, Toast.LENGTH_SHORT);
                            toast.show();
                            HelperUser.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).updatePlayer(false);
                        }else{
                            Toast toast = Toast.makeText(((AppCompatActivity)game.getGameEngine().getContext()).getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                SnackbarManager.show(
                        Snackbar.with(((AppCompatActivity)game.getGameEngine().getContext()))
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(((AppCompatActivity)game.getGameEngine().getContext()).getResources().getColor(R.color.colorFondoBoton))
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        subirScore(level,score);
                                    }
                                })
                );
            }
        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("token", HelperUser.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).getToken());
                } catch (ExceptionTokenNull exceptionTokenNull) {

                    HelperUser.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).errorToken(((AppCompatActivity)game.getGameEngine().getContext()));
                }
                params.put("level", level);
                params.put("score", score);
                return params;
            }
        };

        MyVolley.getInstance(((AppCompatActivity)game.getGameEngine().getContext())).add(request);
    }

    @Override
    public void processInput() {
        Touch touch;
        while ((touch = game.getGameEngine().consumeTouch()) != null) {

            int x = touch.getX() * 1000 / game.getScreenWidth();
            int y = touch.getY() * 1000 / game.getScreenHeight();

            if ((y > 700 && y < 900) && (x < 400 && x > 100)) {
                if(touch.isDown()){
                    Scene01 scene = new Scene01(game);
                    game.loadScene(scene);
                }
            }
            else if ((y > 700 && y < 900) && (x < 900 && x > 600)) { game.getGameEngine().finishGame(); }
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.DKGRAY);

        canvas.scale(canvas.getWidth() / 1000.0f,canvas.getHeight() / 1000.0f);
        canvas.drawRect(100, 700, 400, 900, paintKeyBackground);
        canvas.drawText(aceptar, 250 - paintKeySymbol.measureText(aceptar) / 2, 820, paintKeySymbol);
        canvas.drawRect(600, 700, 900, 900, paintKeyBackground);
        canvas.drawText(rechazar, 750 - paintKeySymbol.measureText(rechazar) / 2, 820, paintKeySymbol);

        canvas.drawText(pregunta, 500 - paintQuestion.measureText(pregunta) / 2, 200, paintQuestion );

        if(this.puntuacion != null){
            String texto = App.getContext().getString(R.string.puntuacionPregunta)+" " + this.puntuacion;
            canvas.drawText(texto, 500 - paintScore.measureText(texto) / 2, 400, paintScore);
        }
    }
}
