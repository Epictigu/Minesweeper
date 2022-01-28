package def.fhswf.ma.minesweeper.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;
import def.fhswf.ma.minesweeper.manager.HighscoreManager;
import def.fhswf.ma.minesweeper.manager.MineManager;
import def.fhswf.ma.minesweeper.manager.PointManager;
import def.fhswf.ma.minesweeper.manager.TimeManager;
import def.fhswf.ma.minesweeper.ui.MinesweeperPane;

public class MineSweeperGame {

    private MinesweeperPane minesweeperPane;

    private final Random random = new Random();
    private boolean gameOver = false;

    private Difficulty difficulty;
    private int gameState = 0;

    private int rows;
    private int columns;

    private int[][] state = null;
    private int[][] value = null;

    private TimeManager timeManager = new TimeManager();

    public MineSweeperGame(MinesweeperPane minesweeperPane, int rows, int columns, Difficulty difficulty){
        this.minesweeperPane = minesweeperPane;

        this.difficulty = difficulty;
        this.rows = rows;
        this.columns = columns;

        state = new int[rows][columns];

        for(int[] i : state){
            Arrays.fill(i, 0);
        }
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public boolean isGenerated(){
        return value != null;
    }

    public int getValue(int row, int column){
        return value[row][column];
    }

    public int getState(int row, int column){
        return state[row][column];
    }

    public int getRows(){
        return rows;
    }

    public int getColumns(){
        return columns;
    }

    public TimeManager getTimeManager(){
        return timeManager;
    }

    public void clickField(int row, int column){
        int v = value[row][column];
        if(v == -1) {
            state[row][column] = 3;
            gameState = 1;
            finishGame();
            for(int r = 0; r < rows; r++){
                for(int c = 0; c < columns; c++){
                    if(r == row && c == column)
                        continue;
                    if(value[r][c] == -1)
                        state[r][c] = 2;
                }
            }
            timeManager.stopTimer();
        } else if(v == 0){
            state[row][column] = 4;
            for(int eRow = -1; eRow < 2; eRow++){
                for(int eColumn = -1; eColumn < 2; eColumn++){
                    if((eRow == 0 && eColumn == 0) || eRow + row < 0 || eRow + row > rows - 1
                            || eColumn + column < 0 || eColumn + column > columns - 1)
                        continue;
                    if(state[eRow + row][eColumn + column] == 0)
                        clickField(eRow + row, eColumn + column);
                }
            }
            PointManager.getInstance().addPoints(1);
        } else if(v > 0 && v < 9){
            state[row][column] = 4;
            PointManager.getInstance().addPoints(1);
        }
        if(!gameOver){
            int emptyFields = 0;
            for(int r = 0; r < rows; r++){
                for(int c = 0; c < columns; c++){
                    if(state[r][c] == 0 && value[r][c] != -1)
                        emptyFields++;
                }
            }

            if(emptyFields == 0){
                for(int r = 0; r < rows; r++){
                    for(int c = 0; c < columns; c++){
                        if(state[r][c] == 0 && value[r][c] == -1)
                            state[r][c] = 2;
                    }
                }
                gameState = 2;
                finishGame();
                timeManager.stopTimer();
            }
        }
    }

    public void setState(int row, int column, int value){
        state[row][column] = value;
    }

    public void generate(int rowSafe, int columnSafe){
        value = new int[rows][columns];

        List<Point> availablePoints = new ArrayList<Point>();
        int x = 0, y = 0;
        for(int[] i : value){
            y = 0;
            Arrays.fill(i, 0);
            for(int j : i){
                if(!(x == rowSafe && y == columnSafe))
                    availablePoints.add(new Point(x, y));
                y++;
            }
            x++;
        }

        for(int i = 0; i < MineManager.getInstance().getMaxMines(); i++){
            Point p = availablePoints.get(random.nextInt(availablePoints.size()));
            availablePoints.remove(p);

            value[p.x][p.y] = -1;
        }

        x = 0;
        for(int[] i : value){
            y = 0;
            for(int j : i){
                if(value[x][y] != -1){
                    int bombsFound = 0;
                    for(int row = -1; row < 2; row++){
                        for(int column = -1; column < 2; column++){
                            if((row == 0 && column == 0) || row + x < 0 || row + x > rows - 1
                                    || column + y < 0 || column + y > columns - 1)
                                continue;
                            if(value[x + row][y + column] == -1)
                                bombsFound++;
                        }
                    }
                    value[x][y] = bombsFound;
                }
                y++;
            }
            x++;
        }

        timeManager.startTimer();
    }

    private void finishGame(){
        this.gameOver = true;
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){
                switch(state[r][c]){
                    case 3:
                        PointManager.getInstance().removePoints(5);
                        break;
                    case 1:
                        if(value[r][c] == -1){
                            PointManager.getInstance().addPoints(5);
                        } else {
                            PointManager.getInstance().removePoints(3);
                        }
                        break;
                    case 2:
                        if(value[r][c] == -1 && gameState == 2){
                            PointManager.getInstance().addPoints(5);
                        }
                        break;
                }
            }
        }

        showFinishMessage();

        EditText input = new EditText(minesweeperPane.getContext());

        Highscore placeholder = new Highscore("Placeholder", PointManager.getInstance().getPoints(), timeManager.getTime());
        try {
            if(difficulty != Difficulty.BENUTZERDEFINIERT && HighscoreManager.getInstance().checkHighscore(placeholder, difficulty)){
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
                                    showIncorrectInputAlert("Das Namensfeld darf nicht leer sein.");
                                    return;
                                }
                                if(inputText.length() > 16){
                                    showIncorrectInputAlert("Der Name darf nicht länger als 16 Buchstaben sein.");
                                    return;
                                }

                                new Thread(){
                                    public void run(){
                                        try {
                                            HighscoreManager.getInstance().addHighscore(new Highscore(input.getText().toString(), PointManager.getInstance().getPoints(), timeManager.getTime()), difficulty);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showIncorrectInputAlert(String message){
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

    private void showFinishMessage(){
        if(gameState == 1){
            showDialog("Verloren", "Eine Bombe wurde angeklickt. Das Spiel ist verloren!");
        } else if(gameState == 2){
            showDialog("Gewonnen", "Alle Felder wurden gefunden. Das Spiel ist gewonnen!");
        }
    }

    private void showDialog(String title, String message){
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
                        minesweeperPane.startNewGame();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
