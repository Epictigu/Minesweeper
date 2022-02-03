package def.fhswf.ma.minesweeper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Eine kurze Anleitung für das Spiel
 *
 * Autor: Marcus Nolzen
 */
public class AnleitungActivity extends AppCompatActivity {
    /**
     * Exit Button und Text hinzufügen
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anleitung);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }
}