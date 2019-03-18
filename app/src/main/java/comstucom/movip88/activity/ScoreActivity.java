package comstucom.movip88.activity;

import android.content.pm.ActivityInfo;
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

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import comstucom.movip88.HelperUser;
import comstucom.movip88.R;
import comstucom.movip88.model.Player;
import comstucom.movip88.model.Score;

/**
 * Activiti encargada de gestionar la vista de los nives que has jugado
 */
public class ScoreActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView nombrePlayer;
    ImageView imagePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        recyclerView = findViewById(R.id.recyclerViewScore);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        nombrePlayer = findViewById(R.id.nombrePlayer);
        imagePlayer = findViewById(R.id.imagePlayer);

    }

    /**
     * coje el historial de niveles de la instancia de jugador en el helper user y monta el rycler view
     */
    @Override
    public void onResume() {
        super.onResume();
        nombrePlayer.setText(HelperUser.getInstance(this).getPlayer().getPlayerName());
        Picasso.get().load(HelperUser.getInstance(this).getPlayer().getProfilePicture()).into(imagePlayer);
        recyclerView.setAdapter(new ScoreAdapter(HelperUser.getInstance(this).getPlayer().getHistorial()));
    }

    /**
     * Holder para realizar la view de un item de tus niveles jugados
     */
    class ScoreViewHolder extends RecyclerView.ViewHolder {

        TextView level;
        TextView score;
        TextView playedAt;

        ScoreViewHolder(@NonNull View scoreView) {
            super(scoreView);
            level = scoreView.findViewById(R.id.level);
            score = scoreView.findViewById(R.id.score);
            playedAt = scoreView.findViewById(R.id.playedAt);
        }

    }

    class ScoreAdapter extends RecyclerView.Adapter<ScoreViewHolder> {

        private List<Score> levels;

        ScoreAdapter(List<Score> Score) {
            super();
            this.levels = Score;
            Collections.sort(this.levels);
        }

        @NonNull @Override
        public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.socore_item, parent, false);

            return new ScoreViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ScoreViewHolder viewHolder, int position) {
            Score s = levels.get(position);
            viewHolder.level.setText(""+s.getLevel());
            viewHolder.score.setText(""+s.getScore());
            viewHolder.playedAt.setText(""+s.getTimePLayed());
        }
        @Override
        public int getItemCount() {
            return levels.size();
        }


    }
}
