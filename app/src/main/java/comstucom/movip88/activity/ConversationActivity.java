package comstucom.movip88.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comstucom.movip88.APIResponse;
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.exception.ExceptionTokenNull;
import comstucom.movip88.model.Message;
import comstucom.movip88.model.Player;

import static java.lang.Thread.sleep;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ConversationActivity extends AppCompatActivity {

    int id;
    ImageView fotoUserCon;
    TextView nombreUserCon;
    EditText messadeEditText;
    RecyclerView recyclerViewMessages;
    MessagesAdapter adapter = new MessagesAdapter();
    final Handler h = new Handler();
    Runnable myRunnable;
    Integer currentPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        id = getIntent().getIntExtra("id",0);
        fotoUserCon = findViewById(R.id.fotoUserCon);
        nombreUserCon = findViewById(R.id.nameUserCon);
        messadeEditText = findViewById(R.id.messadeEditText);
        recyclerViewMessages = findViewById(R.id.recyclerViewCon);
        recyclerViewMessages.setAdapter(adapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setItemAnimator(new DefaultItemAnimator());
        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage(HelperUser.getInstance(ConversationActivity.this).getToken(), messadeEditText.getText().toString());
                    messadeEditText.setText("");
                } catch (ExceptionTokenNull exceptionTokenNull) {
                    HelperUser.getInstance(ConversationActivity.this).errorToken(ConversationActivity.this);
                }
            }
        });
        //lisener para guardar la posidon donde estas para que cuando se actualice la lista
        recyclerViewMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    currentPosition = ((LinearLayoutManager)recyclerViewMessages.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                }else if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    currentPosition = ((LinearLayoutManager)recyclerViewMessages.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadInfoUser();

        //inicializa un runable para ir basjando los mensajes cada segundo
        myRunnable = new Runnable() {
            @Override
            public void run() {
                downloadMessages();
                h.postDelayed(this,1000);
            }
        };

        h.postDelayed(myRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //elimina les trucades als runable
        h.removeCallbacks(myRunnable);
    }

    final static String URL_SEND_MESSAGE = "https://api.flx.cat/dam2game/message";

    public void sendMessage(final String token, final String messageSending) {
        StringRequest request = new StringRequest(Request.Method.PUT, URL_SEND_MESSAGE+"/"+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Boolean>>() {}.getType();
                        APIResponse<Boolean> apiResponse = gson.fromJson(response, typeToken);
                        if(apiResponse.getErrorCode() != 0){
                            if (apiResponse.getErrorCode() == 2){
                                HelperUser.getInstance(ConversationActivity.this).errorToken(ConversationActivity.this);
                            }else {
                                Toast toast = Toast.makeText(getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("token", token);
                params.put("text", messageSending);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);
    }

    final static String URL_GET_USER_INFO = "https://api.flx.cat/dam2game/user/";

        private void downloadInfoUser() {
        String token = "";
        try {
            token = HelperUser.getInstance(this).getToken();
        } catch (ExceptionTokenNull exceptionTokenNull) {
            HelperUser.getInstance(this).errorToken(this);
        }
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_USER_INFO+"/"+id+"?token="+token,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<Player>>() {}.getType();
                        APIResponse<Player> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                            HelperUser.getInstance(ConversationActivity.this).errorToken(ConversationActivity.this);
                        }else if (apiResponse.getErrorCode() == 0){
                            Player jugador = apiResponse.getData();
                            Picasso.get().load(jugador.getProfilePicture()).into(fotoUserCon);
                            nombreUserCon.setText(jugador.getPlayerName());
                        }else{
                            Toast toast = Toast.makeText(ConversationActivity.this.getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                    Toast toast = Toast.makeText(ConversationActivity.this.getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        MyVolley.getInstance(this.getApplicationContext()).add(request);
    }

    final static String URL_GET_MESSGES = "https://api.flx.cat/dam2game/message";

    private void downloadMessages() {
        String token = "";
        try {
            token = HelperUser.getInstance(ConversationActivity.this).getToken();
        } catch (ExceptionTokenNull exceptionTokenNull) {
            HelperUser.getInstance(ConversationActivity.this).errorToken(ConversationActivity.this);
        }
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_MESSGES+"/"+id+"?token="+token,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<Message>>>() {}.getType();
                        APIResponse<List<Message>> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                            HelperUser.getInstance(ConversationActivity.this).errorToken(ConversationActivity.this);
                        }else if (apiResponse.getErrorCode() == 0) {
                            List<Message> messages = apiResponse.getData();
                            adapter.setMessages(messages);
                            adapter.notifyDataSetChanged();
                            recyclerViewMessages.smoothScrollToPosition(currentPosition == null ? messages.size() - 1 < 0 ? 0 : messages.size() - 1 : (currentPosition + adapter.getNuevosMensages()));
                        }else{
                            Toast toast = Toast.makeText(ConversationActivity.this.getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String errirMessage = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    errirMessage = response.statusCode + " " + errirMessage;
                    Toast toast = Toast.makeText(ConversationActivity.this.getApplicationContext(), errirMessage, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        MyVolley.getInstance(ConversationActivity.this.getApplicationContext()).add(request);
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView messageView;
        TextView horaView;
        TextView checkView;
        Space spaceLeft;
        Space spaceRight;

        MessagesViewHolder(@NonNull View rankingView) {
            super(rankingView);
            messageView = rankingView.findViewById(R.id.messageView);
            horaView = rankingView.findViewById(R.id.horaView);
            checkView = rankingView.findViewById(R.id.checkView);
            spaceLeft = rankingView.findViewById(R.id.spaceLeft);
            spaceRight = rankingView.findViewById(R.id.spaceRight);
        }
    }

    class MessagesAdapter extends RecyclerView.Adapter<MessagesViewHolder> {

        private List<Message> messages;
        private int nuevosMensages = 0;

        public MessagesAdapter() {
            this.messages = new ArrayList();
        }

        public void setMessages(List<Message> messages) {
            nuevosMensages = messages.size() - this.messages.size();
            this.messages = messages;
        }

        public int getNuevosMensages() {
            int numMen = this.nuevosMensages;
            this.nuevosMensages = 0;
            return numMen;
        }

        @NonNull
        @Override
        public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.message_item, parent, false);

            return new MessagesViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull MessagesViewHolder viewHolder, int position) {
            Message m = messages.get(position);
            viewHolder.horaView.setText(LocalDateTime.parse(m.getSentAt(), ISO_OFFSET_DATE_TIME).format(DateTimeFormatter.ofPattern("H:m:s")));
            viewHolder.messageView.setText(m.getText());
            viewHolder.checkView.setTextColor(m.getReceivedAt()==null ? Color.GRAY : Color.BLUE);

            int align;
            if(m.getFromId() == HelperUser.getInstance(ConversationActivity.this).getPlayer().getId()){
                viewHolder.spaceLeft.setVisibility(View.VISIBLE);
                viewHolder.spaceRight.setVisibility(View.GONE);
                align = Gravity.RIGHT;
                viewHolder.messageView.setBackgroundResource(R.drawable.bubble_out);
            }else{
                viewHolder.spaceLeft.setVisibility(View.GONE);
                viewHolder.spaceRight.setVisibility(View.VISIBLE);
                align = Gravity.LEFT;
                viewHolder.messageView.setBackgroundResource(R.drawable.bubble_in);
            }
            viewHolder.horaView.setGravity(align);
            viewHolder.messageView.setGravity(align);
            viewHolder.checkView.setGravity(align);
        }
        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
