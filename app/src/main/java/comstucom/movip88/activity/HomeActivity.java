package comstucom.movip88.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import comstucom.movip88.R;

public class HomeActivity extends AppCompatActivity {

    /**
     * Funcions per a que els botons del menú principal portin a les activities corresponents
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
        Button btnRank = findViewById(R.id.btnRnk);
        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });
        Button btnSet = findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        Button btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Aboutus.class);
                startActivity(intent);
            }
        });

    }
}
