package def.fhswf.ma.minesweeper.manager;

import android.widget.TextView;

import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;

public class MineManager {

    private static MineManager instance;
    public static MineManager getInstance(){
        if(instance == null)
            instance = new MineManager();
        return instance;
    }

    private int maxMines = 10;
    private int hitMines = 0;

    private MineManager(){}

    public void setMaxMines(int mines){
        this.maxMines = mines;
        updateLable();
    }

    public void setHitMines(int mines){
        this.hitMines = mines;
        updateLable();
    }

    public Integer getMaxMines(){
        return maxMines;
    }

    public Integer getHitMines(){
        return hitMines;
    }

    private void updateLable(){
        ((TextView)MainActivity.getInstance().findViewById(R.id.mineView)).setText(hitMines + "/" + maxMines);
    }

}
