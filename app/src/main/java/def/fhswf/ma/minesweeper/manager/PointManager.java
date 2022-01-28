package def.fhswf.ma.minesweeper.manager;

import android.widget.TextView;

import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;

/**
 * Autor: Timo Röder
 */
public class PointManager {

    private static PointManager instance;
    public static PointManager getInstance(){
        if(instance == null)
            instance = new PointManager();
        return instance;
    }

    private int points = 0;

    private PointManager(){}

    public void addPoints(int points){
        this.points+=points;
        updateLable();
    }

    public void removePoints(int points){
        this.points-=points;
        updateLable();
    }

    public void resetPoints(){
        this.points = 0;
        updateLable();
    }

    public int getPoints(){
        return points;
    }

    private void updateLable(){
        ((TextView) MainActivity.getInstance().findViewById(R.id.pointsView)).setText(String.format("%03d", this.points));
    }

}
