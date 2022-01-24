package def.fhswf.ma.minesweeper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;
import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.highscore.Highscore;
import def.fhswf.ma.minesweeper.manager.HighscoreManager;
import def.fhswf.ma.minesweeper.manager.MineManager;
import def.fhswf.ma.minesweeper.manager.PointManager;
import def.fhswf.ma.minesweeper.manager.TimeManager;

public class MinesweeperPane extends View {

    private final Random random = new Random();

    private int rows = 9;
    private int columns = 9;

    private final int rows_for_size = 9;
    private final int columns_for_size = 9;

    private Camera camera;
    //private float xOffset = 0;
    //private float yOffset = 0;

    private float paneWidth;
    private float paneHeight;

    private int[][] state = null;
    private int[][] value = null;

    private boolean gameOver = false;

    private final Map<Integer, Integer> numberColors = new HashMap<Integer, Integer>();

    private Point downPoint = null;
    //private Point movePoint = null;
    private final Handler handler = new Handler();
    private TimeManager timeManager = new TimeManager();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(downPoint != null){
                float width = paneWidth;
                float height = paneHeight;

                float ySet = (0.0f + getCutHeight() - height) / 2;
                if(ySet < 0){
                    ySet = 0;
                }

                int rowIndex = (int) ((((float)downPoint.y) - ySet - camera.getYOffset()) / (height / rows));
                int columnIndex = (int) (((float)downPoint.x - camera.getXOffset()) / (width / columns));

                if(state[rowIndex][columnIndex] == 0) {
                    state[rowIndex][columnIndex] = 1;
                    MineManager.getInstance().setHitMines(MineManager.getInstance().getHitMines() + 1);
                } else if(state[rowIndex][columnIndex] == 1) {
                    state[rowIndex][columnIndex] = 0;
                    MineManager.getInstance().setHitMines(MineManager.getInstance().getHitMines() - 1);
                }
                downPoint = null;
                invalidate();
            }
        }
    };

    public MinesweeperPane(Context context, AttributeSet attrs) {
        super(context, attrs);

        numberColors.put(1, getResources().getColor(R.color.numberOne));
        numberColors.put(2, getResources().getColor(R.color.numberTwo));
        numberColors.put(3, getResources().getColor(R.color.numberThree));
        numberColors.put(4, getResources().getColor(R.color.numberFour));
        numberColors.put(5, getResources().getColor(R.color.numberFive));
        numberColors.put(6, getResources().getColor(R.color.numberSix));
        numberColors.put(7, getResources().getColor(R.color.numberSeven));
        numberColors.put(8, getResources().getColor(R.color.numberEight));

        state = new int[rows][columns];

        for(int[] i : state){
            Arrays.fill(i, 0);
        }

        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if(viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    createCamera();
                }
            });
        }

    }

    private void createCamera(){
        camera = new Camera(getWidth() / columns_for_size * columns,
                paneWidth * ((float)rows / columns),
                getWidth(),
                getCutHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setColor(getResources().getColor(R.color.scheme_3));
        canvas.drawRect(0, 0, getWidth(), getCutHeight(), paint);

        float width = canvas.getWidth() * ((float) columns / columns_for_size);
        float height = width * ((float) rows / columns);

        float ySet = (0.0f + getCutHeight() - height) / 2;
        if(ySet < 0)
            ySet = 0;

        float xW = width / columns;
        float yW = height / rows;

        float xW10 = xW * 0.1f;
        float xW80 = xW * 0.8f;

        float yW10 = yW * 0.1f;
        float yW80 = yW * 0.8f;

        paint.setColor(Color.LTGRAY);
        canvas.drawRect(camera.getXOffset(), camera.getYOffset() + ySet, camera.getXOffset() + width, camera.getYOffset() + height + ySet, paint);

        paint.setColor(Color.GRAY);
        for(int row = 1; row < rows; row++){
            canvas.drawRect(camera.getXOffset() + 0, camera.getYOffset() + ySet + yW * row - 5, camera.getXOffset() + width, camera.getYOffset() + ySet + yW * row + 5, paint);
        }

        for(int column = 1; column < columns; column++){
            canvas.drawRect(camera.getXOffset() + xW * column - 5, camera.getYOffset() + ySet, camera.getXOffset() + xW * column + 5, camera.getYOffset() + height + ySet, paint);
        }

        for(int row = 0; row < rows; row++){
            for(int column = 0; column < columns; column++){
                int s = state[row][column];

                float x = width / columns * column;
                float y = ySet + height / rows * row;

                if(s == 0 || s == 1){
                    paint.setColor(getResources().getColor(R.color.topLeftSweeperColor));

                    Path path = new Path();
                    path.moveTo(camera.getXOffset() + x, camera.getYOffset() + y);
                    path.lineTo(camera.getXOffset() + x + xW, camera.getYOffset() + y);
                    path.lineTo(camera.getXOffset() + x, camera.getYOffset() + y + yW);
                    path.lineTo(camera.getXOffset() + x, camera.getYOffset() + y);
                    path.close();

                    canvas.drawPath(path, paint);

                    paint.setColor(getResources().getColor(R.color.bottomRightSweeperColor));

                    Path path2 = new Path();
                    path2.moveTo(camera.getXOffset() + x, camera.getYOffset() + y + yW);
                    path2.lineTo(camera.getXOffset() + x + xW, camera.getYOffset() + y + yW);
                    path2.lineTo(camera.getXOffset() + x + xW, camera.getYOffset() + y);
                    path2.lineTo(camera.getXOffset() + x, camera.getYOffset() + y + yW);
                    path2.close();

                    canvas.drawPath(path2, paint);

                    paint.setColor(getResources().getColor(R.color.mainSweeperColor));
                    canvas.drawRect(camera.getXOffset() + x + xW10, camera.getYOffset() + y + yW10, camera.getXOffset() + x + xW80 + xW10, camera.getYOffset() + y + yW80 + yW10, paint);

                    if(s == 1){
                        Drawable flagIcon = getResources().getDrawable(R.mipmap.flag_icon_foreground);
                        flagIcon.setBounds((int) (camera.getXOffset() + x + xW10 * 2f), (int) (camera.getYOffset() + y + yW10 * 2f), (int) (camera.getXOffset() + x + xW80), (int) (camera.getYOffset() + y + yW80));
                        flagIcon.draw(canvas);
                    }
                } else if(s == 2 || s == 3) {
                    if(s == 3) {
                        paint.setColor(Color.RED);
                        canvas.drawRect(camera.getXOffset() + x, camera.getYOffset() + y, camera.getXOffset() + x + xW, camera.getYOffset() + y + yW, paint);
                    }

                    Drawable flagIcon = getResources().getDrawable(R.mipmap.bomb_icon_foreground);
                    flagIcon.setBounds((int) (camera.getXOffset() + x + xW10 * 2f), (int) (camera.getYOffset() + y + yW10 * 2f), (int) (camera.getXOffset() + x + xW80), (int) (camera.getYOffset() + y + yW80));
                    flagIcon.draw(canvas);
                } else if(s == 4){
                    int v = value[row][column];
                    if(v > 0 && v <= 8){
                        Rect bounds = new Rect();
                        paint.setTypeface(Typeface.DEFAULT_BOLD);

                        paint.setTextSize(48f);
                        paint.setLinearText(true);
                        paint.setSubpixelText(true);
                        paint.getTextBounds(v + "", 0, 1, bounds);

                        paint.setColor(numberColors.get(v));
                        paint.setTextSize(48f * (yW80 - yW10 * 2) / (float)bounds.height());
                        paint.getTextBounds(v + "", 0, 1, bounds);
                        float sizeToCenter = (((xW80 - xW10 * 2) - bounds.width()) / 2);
                        float coord = x + (xW10 * 2) + sizeToCenter;

                        float xSet = 0;
                        if(v == 1)
                            xSet-=10;

                        canvas.drawText(String.valueOf(v), camera.getXOffset() + coord + xSet, camera.getYOffset() + y+ (yW80), paint);
                    }
                }
            }
        }
    }

    private void handleMouseMove(int x, int y){
        if(downPoint != null)
            if(getDistance(new Point(x, y), downPoint) > 25) {
                camera.setMovePoint(downPoint);
                downPoint = null;
                handler.removeCallbacks(runnable);
            }
        if(camera.isMovePointSet()) {
            camera.move(x, y);
            invalidate();
        }
    }

    public int getCutHeight(){
        return getHeight() - 90;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(gameOver)
            return true;

        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        float width = paneWidth;
        float height = paneHeight;

        float ySet = (0.0f + getCutHeight() - height) / 2;
        if(ySet < 0){
            ySet = 0;
        }

        if(y < ySet || y > ySet + height) {
            if(action == MotionEvent.ACTION_MOVE) {
                handleMouseMove(x, y);
            } else if(action == MotionEvent.ACTION_DOWN){
                downPoint = new Point(x, y);
            }
            return true;
        }

        int rowIndex = (int) ((event.getY() - ySet - camera.getYOffset()) / (height / rows));
        int columnIndex = (int) ((event.getX() - camera.getXOffset()) / (width / columns));

        Point pressPoint = new Point(x, y);

        switch(action){
            case MotionEvent.ACTION_DOWN:
                if(downPoint != null){
                    handler.removeCallbacks(runnable);
                }
                if(camera.isMovePointSet()){
                    camera.setMovePoint(null);
                }
                downPoint = pressPoint;
                handler.postDelayed(runnable, 400);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMouseMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if(value == null && !camera.isMovePointSet()){
                    generate(rowIndex, columnIndex);
                }
                if(downPoint != null){
                    downPoint = null;
                    clickField(rowIndex, columnIndex);
                    handler.removeCallbacks(runnable);
                    invalidate();
                }
                if(camera.isMovePointSet()){
                    camera.setMovePoint(null);
                }
                break;
        }

        return true;
    }

    private void clickField(int row, int column){
        int v = value[row][column];
        if(v == -1) {
            state[row][column] = 3;
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
            //showDialog("Verloren", "Eine Bombe wurde angeklickt. Das Spiel ist verloren!");
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
                finishGame();
                timeManager.stopTimer();
                //showDialog("Gewonnen", "Alle Felder wurden gefunden. Das Spiel ist gewonnen!");
            }
        }
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
                }
            }
        }

        EditText input = new EditText(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setTitle("Namen eingeben").setView(input)
                .setPositiveButton("Absenden", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(){
                    public void run(){
                        try {
                            HighscoreManager.getInstance().addHighscore(new Highscore(input.getText().toString(), PointManager.getInstance().getPoints(), timeManager.getTime()), Difficulty.EINFACH);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                dialog.cancel();
            }
        }).setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        builder.setView(null);
    }

    private void showDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setMessage(message).setTitle(title)
                .setPositiveButton("Neues Spiel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startNewGame();
                        //clearBoard();
                    }
                })
                .setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private Double getDistance(Point p1, Point p2){
        float xDistance = p2.x - p1.x;
        float yDistance = p2.y - p1.y;
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    public void startNewGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setTitle("Schwierigkeit auswählen")
                .setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(which == 0){
                            rows = 9;
                            columns = 9;
                            MineManager.getInstance().setMaxMines(10);
                        } else if(which == 1){
                            rows = 16;
                            columns = 16;
                            MineManager.getInstance().setMaxMines(40);
                        } else if(which == 2){
                            rows = 30;
                            columns = 16;
                            MineManager.getInstance().setMaxMines(99);
                        }
                        clearBoard();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void generate(int rowSafe, int columnSafe){
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

    public void clearBoard(){
        state = new int[rows][columns];

        for(int[] i : state){
            Arrays.fill(i, 0);
        }
        value = null;

        gameOver = false;

        downPoint = null;
        camera.setMovePoint(null);

        paneWidth = getWidth() / columns_for_size * columns;
        paneHeight = paneWidth * ((float)rows / columns);

        createCamera();

        timeManager.stopTimer();
        timeManager.clearTimer();
        timeManager = new TimeManager();
        PointManager.getInstance().resetPoints();
        MineManager.getInstance().setHitMines(0);
        invalidate();
    }

}
