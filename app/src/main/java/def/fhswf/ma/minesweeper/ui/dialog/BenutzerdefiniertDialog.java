package def.fhswf.ma.minesweeper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import def.fhswf.ma.minesweeper.R;

public class BenutzerdefiniertDialog extends Dialog {

    public BenutzerdefiniertDialog(Context context) {
        super(context);
    }

    private SeekBar rowBar;
    private SeekBar columnBar;
    private SeekBar mineBar;

    private TextView mineCount;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.benutzerdefiniert_dialog);

        rowBar = (SeekBar) findViewById(R.id.rowBar);
        TextView rowCount = (TextView) findViewById(R.id.rowCount);
        rowBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rowCount.setText("" + (progress + 9));
                updateMineMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        columnBar = (SeekBar) findViewById(R.id.columnBar);
        TextView columnCount = (TextView) findViewById(R.id.columnCount);
        columnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                columnCount.setText("" + (progress + 9));
                updateMineMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mineBar = (SeekBar) findViewById(R.id.mineBar);
        mineCount = (TextView) findViewById(R.id.mineCount);
        mineBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mineCount.setText("" + (progress + 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ((Button)findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        updateMineMax();
        submitButton = (Button)findViewById(R.id.submitButton);
    }

    private void updateMineMax(){
        mineBar.setMax((rowBar.getProgress() + 9) * (columnBar.getProgress() + 9) - 12);
        mineCount.setText("" + (mineBar.getProgress() + 10));
    }

    public SeekBar getRowBar(){
        return rowBar;
    }

    public SeekBar getColumnBar() {
        return columnBar;
    }

    public SeekBar getMineBar() {
        return mineBar;
    }

    public Button getSubmitButton() {
        return submitButton;
    }
}
