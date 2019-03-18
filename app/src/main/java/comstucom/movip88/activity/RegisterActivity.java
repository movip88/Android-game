package comstucom.movip88.activity;

import android.app.Activity;
import android.content.Intent;
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
import java.util.regex.Pattern;

import comstucom.movip88.APIResponse;
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.exception.ExceptionTokenNull;

public class RegisterActivity extends AppCompatActivity {

    EditText emailIn;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailIn = findViewById(R.id.emailIn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        Button btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, Aboutus.class);
                startActivity(intent);
            }
        });
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmail()){
                    registerEmail(emailIn.getText().toString());
                }
            }
        });

    }

    private boolean checkEmail(){
        String emailPattern = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@" +
                "[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
        if(!Pattern.matches(emailPattern, emailIn.getText().toString())){
            emailIn.setError(getString(R.string.formatEmail));
            emailIn.setFocusableInTouchMode(true);
            return false;
        }
        return true;
    }

    /**
     * Si existe un token guardado en el shared preferences abre la view del menu ya que estas registrado si no se queda a la espera de que introduzcas algo por comandos
     */
    @Override
    public void onResume() {
        super.onResume();
        String token = null;
        try {
            token = HelperUser.getInstance(this).getToken();
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } catch (ExceptionTokenNull exceptionTokenNull) {
        }
    }

    final static String URL_REGISTER = "https://api.flx.cat/dam2game/register";

    /**
     * Funcion encargada de realizar una peticion a la api para registrar el email cuando recive una respuesta con el id del usuario abre la activity de verificar pasandole el email en el intent
     * @param email - String
     */
    public void registerEmail(final String email) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<String>>() {}.getType();
                        APIResponse<String> apiResponse = gson.fromJson(response, typeToken);
                        if(apiResponse.getErrorCode() == 0){
                            Intent intent = new Intent(RegisterActivity.this, VerificarCodigoActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "ERROR " + apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                SnackbarManager.show(
                        Snackbar.with(RegisterActivity.this)
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(getResources().getColor(R.color.colorFondoBoton))
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        registerEmail(email);
                                    }
                                })
                );
            }
        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }
}
