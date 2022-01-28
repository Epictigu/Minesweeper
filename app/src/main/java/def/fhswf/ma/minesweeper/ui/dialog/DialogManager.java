package def.fhswf.ma.minesweeper.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import def.fhswf.ma.minesweeper.HighscoreActivity;
import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;
import def.fhswf.ma.minesweeper.game.MineSweeperGame;
import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;
import def.fhswf.ma.minesweeper.manager.HighscoreManager;
import def.fhswf.ma.minesweeper.manager.MineManager;
import def.fhswf.ma.minesweeper.manager.PointManager;
import def.fhswf.ma.minesweeper.ui.MinesweeperPane;

/**
 * Autor: Marcus Nolzen
 */
public class DialogManager {

    private static DialogManager instance;
    public static DialogManager getInstance(){
        if(instance == null)
            instance = new DialogManager();
        return instance;
    }

    private DialogManager(){}

    public void showNewGameDialog(MinesweeperPane context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setMessage(message).setTitle(title)
                .setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Neues Spiel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startNewGameDialog(context);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showIncorrectInputAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setMessage(message).setTitle("Inkorrekte Eingabe!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showHighscoreInputDialog(MineSweeperGame mineSweeperGame){
        EditText input = new EditText(mineSweeperGame.getMinesweeperPane().getContext());

        Highscore placeholder = new Highscore("Placeholder", PointManager.getInstance().getPoints(), mineSweeperGame.getTimeManager().getTime());
        new Thread(){
            public void run(){
                try {
                    if(mineSweeperGame.getDifficulty() != Difficulty.BENUTZERDEFINIERT && HighscoreManager.getInstance().checkHighscore(placeholder, mineSweeperGame.getDifficulty())){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
                                builder.setTitle("Namen eingeben").setView(input)
                                        .setPositiveButton("Absenden", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {}
                                        }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alert = builder.create();

                                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        Button button  = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                        button.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View v) {
                                                String inputText = input.getText().toString();
                                                if(inputText.length() == 0) {
                                                    DialogManager.getInstance().showIncorrectInputAlert("Das Namensfeld darf nicht leer sein.");
                                                    return;
                                                }
                                                if(inputText.length() > 16){
                                                    DialogManager.getInstance().showIncorrectInputAlert("Der Name darf nicht länger als 16 Buchstaben sein.");
                                                    return;
                                                }

                                                new Thread(){
                                                    public void run(){
                                                        try {
                                                            HighscoreManager.getInstance().addHighscore(new Highscore(input.getText().toString(), PointManager.getInstance().getPoints(), mineSweeperGame.getTimeManager().getTime()), mineSweeperGame.getDifficulty());
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }.start();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                alert.show();
                                builder.setView(null);
                            }
                        });
                    }
                } catch (IOException e) {
                    DialogManager.getInstance().showNoConnectionDialog();
                }
            }
        }.start();
    }

    public void startNewGameDialog(MinesweeperPane minesweeperPane){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setTitle("Schwierigkeit auswählen")
                .setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        int newRows = 0;
                        int newColumns = 0;
                        Difficulty difficulty = Difficulty.BENUTZERDEFINIERT;
                        minesweeperPane.getGame().getTimeManager().clearTimer();
                        if(which == 0){
                            newRows = 9;
                            newColumns = 9;
                            difficulty = Difficulty.EINFACH;
                            MineManager.getInstance().setMaxMines(10);
                        } else if(which == 1){
                            newRows = 16;
                            newColumns = 16;
                            difficulty = Difficulty.MITTEL;
                            MineManager.getInstance().setMaxMines(40);
                        } else if(which == 2){
                            newRows = 30;
                            newColumns = 16;
                            difficulty = Difficulty.SCHWER;
                            MineManager.getInstance().setMaxMines(99);
                        } else if(which == 3){
                            BenutzerdefiniertDialog bdd = new BenutzerdefiniertDialog(minesweeperPane.getContext());
                            bdd.show();
                            bdd.getSubmitButton().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bdd.cancel();
                                    MineManager.getInstance().setMaxMines(bdd.getMineBar().getProgress() + 10);
                                    minesweeperPane.clearBoard(bdd.getRowBar().getProgress() + 9, bdd.getColumnBar().getProgress() + 9, Difficulty.BENUTZERDEFINIERT);
                                }
                            });
                            return;
                        }

                        minesweeperPane.clearBoard(newRows, newColumns, difficulty);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showNoConnectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setMessage("Es konnte keine Verbindung zum Server aufgebaut werden. Bitte überprüfen Sie ihr Internet.").setTitle("Keine Verbindung!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
