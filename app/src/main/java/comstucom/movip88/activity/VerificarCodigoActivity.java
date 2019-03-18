package comstucom.movip88.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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

public class VerificarCodigoActivity extends AppCompatActivity {

    EditText codigoVer;
    TextView debug;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String email = getIntent().getStringExtra("email");
        setContentView(R.layout.activity_verificar_codigo);
        codigoVer = findViewById(R.id.codigoVer);
        debug = findViewById(R.id.debug);
        Button btnVerificar = findViewById(R.id.btnVerificar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarEmail(email ,codigoVer.getText().toString());
            }
        });
    }

    final static String URL_REGISTER_VER = "https://api.flx.cat/dam2game/register";

    /**
     * Codigo enecargado de realizar la petición a la api para la verificación con codigo del email
     * @param email - String
     * @param codigo - String
     */
    public void verificarEmail(final String email, final String codigo) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER_VER,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
                        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);
                        if(apiResponse.getErrorCode() == 0){
                            HelperUser.getInstance(VerificarCodigoActivity.this).setToken(apiResponse.getData());
                            finish();
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                SnackbarManager.show(
                        Snackbar.with(VerificarCodigoActivity.this)
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(getResources().getColor(R.color.colorFondoBoton))
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        verificarEmail(email, codigo);
                                    }
                                })
                );
            }
        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("verify", codigo);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }
}
