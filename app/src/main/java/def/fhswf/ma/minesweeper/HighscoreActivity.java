package def.fhswf.ma.minesweeper;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;
import def.fhswf.ma.minesweeper.manager.HighscoreManager;

public class HighscoreActivity extends AppCompatActivity {

    private HighscoreActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.instance = this;
        setContentView(R.layout.activity_highscore);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Difficulty difficulty = Difficulty.valueOf(tab.getText().toString().toUpperCase());
                setTableContent(difficulty);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(){
            public void run(){
                try {
                    HighscoreManager.getInstance().loadHighscore();
                    setTableContent(Difficulty.EINFACH);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setTableContent(Difficulty difficulty){
        Highscore[] highscores = HighscoreManager.getInstance().getHighscoreByDifficulty(difficulty);
        final int length = (highscores == null) ? 10 : highscores.length;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
                tableLayout.removeAllViews();

                for(int i = 0; i < length; i++){
                    TableRow tableRow = new TableRow(instance, null);
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                    TextView ranking = new TextView(instance, null);
                    ranking.setText((i + 1) + "");
                    TableRow.LayoutParams layoutParamsRanking = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    layoutParamsRanking.weight = 10;
                    ranking.setLayoutParams(layoutParamsRanking);
                    tableRow.addView(ranking);

                    if(highscores != null && highscores[i] != null) {
                        TextView name = new TextView(instance, null);
                        name.setText(highscores[i].getName());
                        TableRow.LayoutParams layoutParamsName = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsName.weight = 50;
                        name.setLayoutParams(layoutParamsName);
                        tableRow.addView(name);

                        TextView points = new TextView(instance, null);
                        points.setText(highscores[i].getPoints() + "");
                        TableRow.LayoutParams layoutParamsPoints = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsPoints.weight = 20;
                        points.setLayoutParams(layoutParamsPoints);
                        tableRow.addView(points);

                        TextView time = new TextView(instance, null);
                        time.setText(highscores[i].getTime() + "");
                        TableRow.LayoutParams layoutParamsTime = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsTime.weight = 20;
                        time.setLayoutParams(layoutParamsTime);
                        tableRow.addView(time);
                    }

                    tableLayout.addView(tableRow);
                }
            }
        });
    }

    public void close(View v){
        finish();
    }

}
