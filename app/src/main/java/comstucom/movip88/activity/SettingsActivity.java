package comstucom.movip88.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import comstucom.movip88.APIResponse;
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.exception.ExceptionTokenNull;

public class SettingsActivity extends AppCompatActivity {
    static ImageView fotoUser;
    EditText editTextNombre;
    static final int PICK_IMAGE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        editTextNombre = findViewById(R.id.editTextNombre);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        //genera una pequeña view con un dialog para borrar la cuenta o cerrar solo la sesion
        Button eliminarCuenta = findViewById(R.id.eliminarbtn);
        eliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                View checkBoxView = View.inflate(SettingsActivity.this, R.layout.delete_data, null);
                final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.deleteData);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
                builder1.setMessage(R.string.deleteServer);
                builder1.setView(checkBoxView);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Delete Account",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                borrarUsuario(checkBox.isChecked());
                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        fotoUser = findViewById(R.id.fotoUser);
        fotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                String[] options = {"galeria", "camara"};
                builder.setTitle("Escoge una opcion");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                startActivityForResult(gallery, PICK_IMAGE);
                                break;
                            case 1:
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SettingsActivity.updatePicture(this);
        editTextNombre.setText(HelperUser.getInstance(this).getPlayer().getPlayerName());
    }

    /**
     * Metodo estatico para actualizar la foto de perfil del usuario
     * @param c
     */
    public static void updatePicture(Context c){
        Picasso.get().load(HelperUser.getInstance(c).getPlayer().getProfilePicture()).into(fotoUser);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!HelperUser.getInstance(this).getPlayer().getPlayerName().equals(editTextNombre.getText().toString())) {
            try {
                subirDato(HelperUser.getInstance(this).getToken(), editTextNombre.getText().toString(), "name");
            } catch (ExceptionTokenNull exceptionTokenNull) {
                HelperUser.getInstance(this).errorToken(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        Bitmap foto = null;
        if(requestCode == PICK_IMAGE){
            try {
                foto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            foto = (Bitmap) extras.get("data");
        }
        try {
            subirDato(HelperUser.getInstance(this).getToken(), encodeTobase64(foto), "image");
        } catch (ExceptionTokenNull exceptionTokenNull) {
            HelperUser.getInstance(this).errorToken(this);
        }
    }

    /**
     * Metodo que recive un bitmap y lo pasa a base64
     * @param image
     * @return
     */
    public String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    final static String URL_UPDATE_USER = "https://api.flx.cat/dam2game/user";

    /**
     * Metodo que realiza la petición a la api para subir la foto el nombre util para las dos yz que es a la misma url
     *
     * @param token - String
     * @param dato - String
     * @param llave - String
     */
    public void subirDato(final String token, final String dato, final String llave) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.PUT, URL_UPDATE_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 0) {
                            HelperUser.getInstance(SettingsActivity.this).updatePlayer(llave.equals("image"));
                        }else if (apiResponse.getErrorCode() == 2){
                            HelperUser.getInstance(SettingsActivity.this).errorToken(SettingsActivity.this);
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                SnackbarManager.show(
                        Snackbar.with(SettingsActivity.this)
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(getResources().getColor(R.color.colorFondoBoton))
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        subirDato(token, dato, llave);
                                    }
                                })
                );
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put(llave, dato);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }

    final static String URL_UNREGISTER_USER = "https://api.flx.cat/dam2game/unregister";

    /**
     * Recibe un booleano para eliminar los datos del servidor o solo cerrar la sesion
     *
     * @param delete - boolean
     */
    public void borrarUsuario(final boolean delete) {
        progressBar.setVisibility(View.VISIBLE);
        final StringRequest request = new StringRequest(Request.Method.POST, URL_UNREGISTER_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                            HelperUser.getInstance(SettingsActivity.this).errorToken(SettingsActivity.this);
                        }else if (apiResponse.getErrorCode() == 0) {
                            HelperUser.getInstance(SettingsActivity.this).deleteAllData();
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.cuentaEliminada, Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(SettingsActivity.this, RegisterActivity.class);
                            SettingsActivity.this.startActivity(intent);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("token", HelperUser.getInstance(SettingsActivity.this).getToken());
                } catch (ExceptionTokenNull exceptionTokenNull) {
                    HelperUser.getInstance(SettingsActivity.this).errorToken(SettingsActivity.this);
                }
                if(delete){
                    params.put("must_delete", "true");
                }
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }
}
