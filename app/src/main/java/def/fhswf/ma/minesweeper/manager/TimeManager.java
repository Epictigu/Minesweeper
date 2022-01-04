package def.fhswf.ma.minesweeper.manager;

import android.os.Handler;
import android.widget.TextView;

import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;

public class TimeManager extends Thread{

    private Handler uiHandler;

    private boolean continueTime = true;

    public TimeManager(){
        uiHandler = new Handler();
    }

    @Override
    public void run() {
        while(continueTime){
            try {
                sleep(1000);
                if(!continueTime)
                    return;

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView timeView = (TextView) MainActivity.getInstance().findViewById(R.id.timeView);
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

    public void startTimer(){
        ((TextView) MainActivity.getInstance().findViewById(R.id.timeView)).setText("000");
        continueTime = true;

        start();
    }

    public void stopTimer(){
        continueTime = false;
    }

    public void clearTimer(){
        ((TextView) MainActivity.getInstance().findViewById(R.id.timeView)).setText("000");
    }

}
