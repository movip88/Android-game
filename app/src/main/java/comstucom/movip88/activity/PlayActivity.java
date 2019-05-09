package comstucom.movip88.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import comstucom.movip88.R;
import comstucom.movip88.custom_view.CustomCuestion;
import comstucom.movip88.game.GameActivity;

public class PlayActivity extends AppCompatActivity {

    private CustomCuestion customCuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play2);
        customCuestion = findViewById(R.id.eligeJuego);
    }

    @Override
    public void onResume() {
        super.onResume();
        customCuestion.setPregunta("A que quieres jugar?");
        customCuestion.setAceptar("Wormy");
        customCuestion.setRechazar("Bonk");
        customCuestion.setVisibility(View.VISIBLE);
        customCuestion.setPreguntaLisener(new CustomCuestion.PreguntaLisener() {
            @Override
            public void aceptar() {
                customCuestion.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(PlayActivity.this, PlayActivityWormy.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void rechazar() {
                customCuestion.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(PlayActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
