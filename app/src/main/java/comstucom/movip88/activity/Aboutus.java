package comstucom.movip88.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import comstucom.movip88.R;

public class Aboutus extends Activity {

    /**
     * Botó location que porta directament a la applicació de Google Maps, mostra la ubicació de STUCOM
     * (més senzill que crear una activity maps y utilitzar la API)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        Button btnMaps = findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<41.3857327>,<2.1650569>?q=<41.3857327>,<2.1650569>(STUCOM CENTRE D'ESTUDIS)"));
                startActivity(intent);
            }
        });
    }



}
