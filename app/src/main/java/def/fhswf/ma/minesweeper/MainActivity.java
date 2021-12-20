package def.fhswf.ma.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new Handler();

        startTimer();
    }

    private void startTimer(){
        new Thread(){
            public void run(){
                while(true){
                    try {
                        sleep(1000);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView timeView = (TextView) findViewById(R.id.timeView);
                                int time = Integer.parseInt(timeView.getText().toString());

                                if(time < 999)
                                    time++;

                                timeView.setText(String.format("%03d", time));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}