package def.fhswf.ma.minesweeper;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;

import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;
import def.fhswf.ma.minesweeper.manager.HighscoreManager;
import def.fhswf.ma.minesweeper.ui.dialog.DialogManager;

public class HighscoreActivity extends AppCompatActivity {

    private static HighscoreActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_highscore);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Difficulty difficulty = Difficulty.valueOf(tab.getText().toString().toUpperCase());
                try {
                    setTableContent(difficulty);
                } catch (IOException e) {
                    System.out.println("Konnte keine Verbindung zum Server aufbauen!");
                    finish();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            DialogManager.getInstance().showNoConnectionDialog();
                        }
                    });
                }
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
                    System.out.println("Konnte keine Verbindung zum Server aufbauen!");
                    finish();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            DialogManager.getInstance().showNoConnectionDialog();
                        }
                    });
                }
            }
        }.start();
    }

    public static HighscoreActivity getInstance(){
        return instance;
    }

    private void setTableContent(Difficulty difficulty) throws IOException {
        Highscore[] highscores = HighscoreManager.getInstance().getHighscoreByDifficulty(difficulty);
        final int length = (highscores == null) ? 10 : highscores.length;

        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    layoutParamsRanking.height = 100;
                    ranking.setAutoSizeTextTypeWithDefaults(ranking.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    ranking.setPadding(0,10,0,0);
                    ranking.setLayoutParams(layoutParamsRanking);
                    tableRow.addView(ranking);

                    if(highscores != null && highscores[i] != null) {
                        TextView name = new TextView(instance, null);
                        String nameS = highscores[i].getName();
                        if(nameS.length() > 16)
                            nameS = nameS.substring(0, 16);
                        name.setText(nameS);
                        TableRow.LayoutParams layoutParamsName = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsName.weight = 50;
                        layoutParamsName.height = 100;
                        name.setAutoSizeTextTypeWithDefaults(name.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        name.setPadding(0,10,0,0);
                        name.setLayoutParams(layoutParamsName);
                        tableRow.addView(name);

                        TextView points = new TextView(instance, null);
                        points.setText(highscores[i].getPoints() + "");
                        TableRow.LayoutParams layoutParamsPoints = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsPoints.weight = 20;
                        layoutParamsPoints.height = 100;
                        points.setAutoSizeTextTypeWithDefaults(points.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        points.setPadding(0,10,0,0);
                        points.setLayoutParams(layoutParamsPoints);
                        tableRow.addView(points);

                        TextView time = new TextView(instance, null);
                        time.setText(highscores[i].getTime() + "s");
                        TableRow.LayoutParams layoutParamsTime = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParamsTime.weight = 20;
                        layoutParamsTime.height = 100;
                        time.setAutoSizeTextTypeWithDefaults(time.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        time.setPadding(0,10,0,0);
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
