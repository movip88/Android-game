package comstucom.movip88.custom_view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import comstucom.movip88.R;

public class CountDownButton extends android.support.v7.widget.AppCompatButton {

    private int cuenta;

    public CountDownButton(Context context) { this(context, null, 0); }
    public CountDownButton(Context context, AttributeSet attrs) { this(context, attrs, 0); }
    public CountDownButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundResource(R.drawable.boton_redondo);
    }

    public void countDown(){
        cuenta = 3;
        final Handler h = new Handler();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                setText("" + cuenta);
                if (cuenta >= 0){
                    cuenta --;
                    h.postDelayed(this,1000);
                }else{
                    setVisibility(INVISIBLE);
                    setText(R.string.btnPlay);
                    if (listener != null) listener.finishedAccount();
                }
            }
        };

        h.postDelayed(myRunnable, 0);
    }

    public interface CountDownListener {
        void finishedAccount();
    }

    private CountDownButton.CountDownListener listener;
    public void setCountDownListener(CountDownButton.CountDownListener listener) {
        this.listener = listener;
    }
}