package comstucom.movip88;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolley {
    // La única instància
    private static MyVolley instance;

    // Obtenir (i crear) la instància
    public static MyVolley getInstance(Context context) {
        if (instance == null) {
            instance = new MyVolley(context.getApplicationContext());
        }
        return instance;
    }

    // La cua
    private RequestQueue queue;

    // Constructor
    private MyVolley(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    // Helper per afegir a la cua
    public <T> void add(Request<T> request) {
        queue.add(request);
    }
}
