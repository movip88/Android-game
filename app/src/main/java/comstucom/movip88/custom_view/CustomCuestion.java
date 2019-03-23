package comstucom.movip88.custom_view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import comstucom.movip88.R;

public class CustomCuestion extends ConstraintLayout {

    private final Button aceptar, rechazar;
    private final TextView pregunta;

    // Constructors
    public CustomCuestion(Context context) {
        this(context, null, 0);
    }
    public CustomCuestion(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustomCuestion(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(this.getContext(), R.layout.alert_pregunta, this);
        aceptar = findViewById(R.id.aceptar);
        aceptar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.aceptar();
            }
        });
        rechazar = findViewById(R.id.rechazar);
        rechazar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.rechazar();
            }
        });
        pregunta = findViewById(R.id.pregunta);
    }

    public void setPregunta(String pregunta){
        this.pregunta.setText(pregunta);
    }

    public void setAceptar(String aceptar){
        this.aceptar.setText(aceptar);
    }

    public void setRechazar(String rechazar){
        this.rechazar.setText(rechazar);
    }

    public interface PreguntaLisener {
        void aceptar();
        void rechazar();
    }

    private CustomCuestion.PreguntaLisener listener;
    public void setPreguntaLisener(CustomCuestion.PreguntaLisener listener) {
        this.listener = listener;
    }
}
