package comstucom.movip88;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import comstucom.movip88.activity.RegisterActivity;
import comstucom.movip88.activity.SettingsActivity;
import comstucom.movip88.exception.ExceptionTokenNull;
import comstucom.movip88.model.Player;

import static android.content.Context.MODE_PRIVATE;

/**
 * Clase creada paragestionar el accesso al shard preferences y de soparte para
 * todo el programa guardandonla instancia del player que esta registrado en l aplicación
 */
public class HelperUser {
    public static HelperUser instance;

    private String token;
    private Player currentPlayer;
    private Context aplicationContext;

    /**
     * metodo para acceder a la unica instancia de la classe recive un contexto como paramatro ya que es neceario para cceder al shared preferences
     * @param c - Context
     * @return - HelperUser
     */
    public static HelperUser getInstance(Context c){
        if(instance == null){
            instance = new HelperUser(c);
        }
        return instance;
    }

    /**
     * Contructor de la clase provado para que solo se pueda llamar desde dentro de la misma clase, busca en el shared si tiene datos para cargar el token si existe y el player si se ha serializado con anterioridad y aparte si existe el token realiza una peticion a la api para actualizar la información
     * @param c - Context
     */
    private HelperUser(Context c){
        this.aplicationContext = c;
        SharedPreferences pref = aplicationContext.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        //coje del shared el token en caso de no encontrarlo devuelve null
        this.token = pref.getString("token", "null");
        String currentPlayer = pref.getString("currentPlayer", "null");
        if(!currentPlayer.equals("null")){
            Gson gson = new Gson();
            this.currentPlayer = gson.fromJson(currentPlayer,Player.class);
        }
        if (!this.token.equals("null")) {
            getUser(this.token, false);
        }
    }

    /**
     * Metodo que se encarga de cojer la variable player de la clase y serializarlo en el shared preferences
     */
    private void serializarPlayer(){
        Gson gson = new Gson();
        SharedPreferences pref = aplicationContext.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed =  pref.edit();
        ed.putString("currentPlayer", gson.toJson(this.currentPlayer));
        ed.apply();
    }

    final static String URL_GET_USER = "https://api.flx.cat/dam2game/user";

    /**
     * Metodo que realiza la conslta del player a la api recive un token para poder realizar la peticion y un boolean para saber si se tiene de actualizar o no, cuando recive true cuando se tiene de actualizar la foto en la ventana de settings
     *
     * @param token - String
     * @param update - boolean
     */
    private void getUser(final String token, final boolean update) {
        if(token.equals("null"))
            return;
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_USER+"?token="+token,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Player>>() {}.getType();
                        APIResponse<Player> apiResponse = gson.fromJson(response, typeToken);
                        if(apiResponse.getErrorCode() == 2){
                            deleteAllData();
                        }else if(apiResponse.getErrorCode() == 0){
                            currentPlayer = apiResponse.getData();
                            serializarPlayer();
                            if (update){
                                SettingsActivity.updatePicture(aplicationContext);
                            }
                        }else{
                            Toast toast = Toast.makeText(aplicationContext.getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                    Toast toast = Toast.makeText(aplicationContext.getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        MyVolley.getInstance(aplicationContext).add(request);
    }

    /**
     * Devuelve un Struing con el token si el token esta en null lanza una exception para controlar que no se puedan realizar consultas a la api
     *
     * @return - String
     * @throws ExceptionTokenNull
     */
    public String getToken() throws ExceptionTokenNull {
        if(this.token.equals("null")){
            throw new ExceptionTokenNull();
        }
        return token;
    }

    public Player getPlayer() {
        return currentPlayer;
    }

    /**
     * Recive String con un token valido y lo guarda en el shared preferences
     *
     * @param token String
     */
    public void setToken(String token) {
        SharedPreferences pref = aplicationContext.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed =  pref.edit();
        ed.putString("token", token);
        ed.apply();
        this.token = token;
        getUser(this.token, false);
    }

    public void updatePlayer(boolean actualizar){
        if (!this.token.equals("null")) {
            getUser(this.token, actualizar);
        }
    }

    /**
     * Botrra todos los datos del shared preferences
     */
    public void deleteAllData(){
        SharedPreferences settings = aplicationContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        settings.edit().clear().apply();
        this.token = "null";
    }

    /**
     * Metodo para ejecutar cuando hay un error de token recive un contexto para poder realizar un intente desde el contexto a la pantalla de registrar
     * @param c - Context
     */
    public void errorToken(Context c){
        deleteAllData();
        Toast toast = Toast.makeText(c.getApplicationContext(), "Vuelve a iniciar sesion porfavor", Toast.LENGTH_LONG);
        toast.show();
        Intent intent = new Intent(c, RegisterActivity.class);
        c.startActivity(intent);
    }
}
