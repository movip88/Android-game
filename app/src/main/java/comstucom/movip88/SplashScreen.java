package comstucom.movip88;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import comstucom.movip88.activity.RegisterActivity;

/**
 * clase que se lanza al principio de la aplicación una vez lanzada tarda 2 segundos en abrir la actividad de registro y cerrarse a si misma sirve como panatalla de presentacion de la apicación
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, RegisterActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();

            }
        }, 2000);
    }
}
