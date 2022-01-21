package def.fhswf.ma.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        ((Button)findViewById(R.id.newGameButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((MinesweeperPane)findViewById(R.id.minesweeperPane)).clearBoard();
                ((MinesweeperPane)findViewById(R.id.minesweeperPane)).startNewGame();
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