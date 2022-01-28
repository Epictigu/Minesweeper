package def.fhswf.ma.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import def.fhswf.ma.minesweeper.manager.HighscoreManager;
import def.fhswf.ma.minesweeper.ui.MinesweeperPane;
import def.fhswf.ma.minesweeper.ui.dialog.DialogManager;

/**
 * Autor: Marcus Nolzen, Dominik Müller, Timo Röder
 */
public class MainActivity extends AppCompatActivity {

    private static  MainActivity instance;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.activity_main);
        new Thread(){
            public void run(){
                try {
                    HighscoreManager.getInstance().loadHighscore();
                } catch (IOException e) {
                    System.err.println("Konnte keine Verbindung zur Highscore aufbauen!");
                }
            }
        }.start();

        ((Button)findViewById(R.id.newGameButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().startNewGameDialog((MinesweeperPane)findViewById(R.id.minesweeperPane));
            }
        });

        ((Button)findViewById(R.id.helpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnleitungActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showHighscoreBoard(View v){
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }

}