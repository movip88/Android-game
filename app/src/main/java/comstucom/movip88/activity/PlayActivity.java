package comstucom.movip88.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.custom_view.CountDownButton;
import comstucom.movip88.custom_view.CustomCuestion;
import comstucom.movip88.custom_view.WormyView;
import comstucom.movip88.exception.ExceptionTokenNull;

/**
 * Encargada de realizar la petición a la api enviando un nivle y una puntuación
 */
public class PlayActivity extends AppCompatActivity implements WormyView.WormyListener, SensorEventListener, CountDownButton.CountDownListener {

    ProgressBar progressBar;
    private WormyView wormyView;
    private SensorManager sensorManager;
    private int coin, dead, knock, intro;
    private SoundPool soundPool;
    private boolean loaded;
    private CountDownButton customPlaybtn;
    private CustomCuestion customCuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        wormyView = findViewById(R.id.wormyView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        customPlaybtn = findViewById(R.id.enviarBTN);
        customPlaybtn.setCountDownListener(this);
        customCuestion = findViewById(R.id.customCuestion);
        customPlaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(PlayActivity.this.intro , 1f, 1f, 1, 0, 1f);
                customPlaybtn.countDown();
            }
        });
        wormyView.setWormyListener(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(15).build();
        }
        else {
            soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        this.coin = soundPool.load(this, R.raw.dinero, 1);
        this.dead = soundPool.load(this, R.raw.perder, 1);
        this.knock = soundPool.load(this,R.raw.golpe,1);
        this.intro = soundPool.load(this,R.raw.golpe,1);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Connect the sensor's listener to the view
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        Log.d("pmv", wormyView.isPlaying() + "");
        if(wormyView.isPlaying()){
            customCuestion.setPregunta("Quieres continuar la partida?");
            customCuestion.setAceptar("Si");
            customCuestion.setRechazar("No");
            customCuestion.setVisibility(View.VISIBLE);
            customCuestion.setPreguntaLisener(new CustomCuestion.PreguntaLisener() {
                @Override
                public void aceptar() {
                    customCuestion.setVisibility(View.INVISIBLE);
                    wormyView.reanudeGame();
                }

                @Override
                public void rechazar() {
                    customCuestion.setVisibility(View.INVISIBLE);
                    customPlaybtn.setVisibility(View.VISIBLE);
                }
            });
        }else{
            customPlaybtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        // Nicely disconnect the sensor's listener from the view
        sensorManager.unregisterListener(this);
        super.onPause();
        wormyView.pauseGame();
    }

    final static String URL_REGISTER_VER = "https://api.flx.cat/dam2game/user/score";

    public void subirScore(final String level, final String score) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER_VER,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                            HelperUser.getInstance(PlayActivity.this).errorToken(PlayActivity.this);
                        }else if(apiResponse.getErrorCode() == 0){
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.setDatosCorrectamente, Toast.LENGTH_SHORT);
                            toast.show();
                            HelperUser.getInstance(PlayActivity.this).updatePlayer(false);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                SnackbarManager.show(
                        Snackbar.with(PlayActivity.this)
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(getResources().getColor(R.color.colorFondoBoton))
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
                    params.put("token", HelperUser.getInstance(PlayActivity.this).getToken());
                } catch (ExceptionTokenNull exceptionTokenNull) {
                    HelperUser.getInstance(PlayActivity.this).errorToken(PlayActivity.this);
                }
                params.put("level", level);
                params.put("score", score);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }

    @Override
    public void scoreUpdated(View view, int score) {
        if (loaded) {
            soundPool.play(this.coin, 1f, 1f, 1, 0, 1f);
        }
    }

    @Override
    public void livesDown(View view) {
        if (loaded) {
            soundPool.play(this.knock, 1f, 1f, 1, 0, 1f);
        }
    }

    @Override
    public void gameLost(View view, int score) {
        subirScore(String.valueOf(0),String.valueOf(score));
        if (loaded) {
            soundPool.play(this.dead, 1f, 1f, 1, 0, 1f);
        }
        customCuestion.setPregunta("Quieres volver a jugar?");
        customCuestion.setAceptar("Si!!");
        customCuestion.setRechazar("No :(");
        customCuestion.setVisibility(View.VISIBLE);
        customCuestion.setPreguntaLisener(new CustomCuestion.PreguntaLisener() {
            @Override
            public void aceptar() {
                customCuestion.setVisibility(View.INVISIBLE);
                wormyView.newGame();
            }

            @Override
            public void rechazar() {
                customCuestion.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(PlayActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];

        wormyView.update(-ax, ay);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void finishedAccount() {
        wormyView.newGame();
    }
}
