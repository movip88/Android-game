package comstucom.movip88.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import comstucom.movip88.APIResponse;
import comstucom.movip88.HelperUser;
import comstucom.movip88.MyVolley;
import comstucom.movip88.R;
import comstucom.movip88.exception.ExceptionTokenNull;
import comstucom.movip88.model.Player;

public class RankingActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadUsers();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadUsers();
    }

    final static String URL_GET_RANKING = "https://api.flx.cat/dam2game/ranking";

    private void downloadUsers() {
        String token = "";
        try {
            token = HelperUser.getInstance(this).getToken();
        } catch (ExceptionTokenNull exceptionTokenNull) {
            HelperUser.getInstance(this).errorToken(this);
        }
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_RANKING+"?token="+token,
                new Response.Listener<String>() {
                    @Override public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<Player>>>() {}.getType();
                        APIResponse<List<Player>> apiResponse = gson.fromJson(response, typeToken);
                        if (apiResponse.getErrorCode() == 2) {
                                HelperUser.getInstance(RankingActivity.this).errorToken(RankingActivity.this);
                        }else if (apiResponse.getErrorCode() == 0) {
                            List<Player> players = apiResponse.getData();
                            Collections.sort(players);
                            recyclerView.setAdapter(new RankingAdapter(players));
                            swipeRefreshLayout.setRefreshing(false);
                        }else{
                            Toast toast = Toast.makeText(RankingActivity.this.getApplicationContext(), apiResponse.getErrorMsg(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                SnackbarManager.show(
                        Snackbar.with(RankingActivity.this)
                                .text(R.string.setProgressBar)
                                .type(SnackbarType.MULTI_LINE)
                                .actionLabel(R.string.setReintentar)
                                .actionColor(getResources().getColor(R.color.colorFondoBoton))
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        downloadUsers();
                                    }
                                })
                );
            }
        });
        MyVolley.getInstance(this.getApplicationContext()).add(request);
    }

    class RankingViewHolder extends RecyclerView.ViewHolder {

        TextView userNameRanking;
        TextView userScoreRanking;
        ImageView userPictureRanking;
        View rankingView;
        int id;

        RankingViewHolder(@NonNull View rankingView) {
            super(rankingView);
            this.rankingView = rankingView;
            userNameRanking = rankingView.findViewById(R.id.userNameRanking);
            userScoreRanking = rankingView.findViewById(R.id.userScoreRanking);
            userPictureRanking = rankingView.findViewById(R.id.userPictureRanking);
            rankingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if(id == HelperUser.getInstance(v.getContext()).getPlayer().getId()){
                        intent = new Intent(RankingActivity.this, ScoreActivity.class);
                    }else{
                        intent = new Intent(RankingActivity.this, ConversationActivity.class);
                        intent.putExtra("id",id);
                    }
                    startActivity(intent);
                }
            });
        }


    }

    class RankingAdapter extends RecyclerView.Adapter<RankingViewHolder> {

        private List<Player> players;

        RankingAdapter(List<Player> players) {
            super();
            this.players = players;
        }

        @NonNull @Override
        public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.ranking_item, parent, false);

            return new RankingViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RankingViewHolder viewHolder, int position) {
            Player p = players.get(position);
            viewHolder.id = p.getId();
            viewHolder.userNameRanking.setText(p.getPlayerName());
            viewHolder.userScoreRanking.setText(""+p.getTotalScore());
            Picasso.get().load(p.getProfilePicture()).into(viewHolder.userPictureRanking);
            viewHolder.rankingView.setBackgroundColor(p.equals(HelperUser.getInstance(RankingActivity.this).getPlayer()) ? getResources().getColor(R.color.colorFondoBoton) : getResources().getColor(R.color.colorVerify) );
        }
        @Override
        public int getItemCount() {
            return players.size();
        }


    }
}
