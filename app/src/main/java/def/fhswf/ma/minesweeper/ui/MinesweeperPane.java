package def.fhswf.ma.minesweeper.ui;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import def.fhswf.ma.minesweeper.Constants;
import def.fhswf.ma.minesweeper.MainActivity;
import def.fhswf.ma.minesweeper.R;
import def.fhswf.ma.minesweeper.game.MineSweeperGame;
import def.fhswf.ma.minesweeper.highscore.Difficulty;
import def.fhswf.ma.minesweeper.manager.MineManager;
import def.fhswf.ma.minesweeper.manager.PointManager;
import def.fhswf.ma.minesweeper.ui.dialog.BenutzerdefiniertDialog;

public class MinesweeperPane extends View {

    private Camera camera;

    private MineSweeperGame game;

    private Point downPoint = null;
    private final Handler handler = new Handler();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(downPoint != null){
                if(game.isGameOver())
                    return;

                float width = camera.getPaneWidth();
                float height = camera.getPaneHeight();

                float ySet = (0.0f + getCutHeight() - height) / 2;
                if(ySet < 0){
                    ySet = 0;
                }

                int rowIndex = (int) ((((float)downPoint.y) - ySet - camera.getYOffset()) / (height / game.getRows()));
                int columnIndex = (int) (((float)downPoint.x - camera.getXOffset()) / (width / game.getColumns()));

                if(game.getState(rowIndex, columnIndex) == 0) {
                    game.setState(rowIndex, columnIndex, 1);
                    MineManager.getInstance().setHitMines(MineManager.getInstance().getHitMines() + 1);
                } else if(game.getState(rowIndex, columnIndex) == 1) {
                    game.setState(rowIndex, columnIndex, 0);
                    MineManager.getInstance().setHitMines(MineManager.getInstance().getHitMines() - 1);
                }
                downPoint = null;
                invalidate();
            }
        }
    };

    public MinesweeperPane(Context context, AttributeSet attrs) {
        super(context, attrs);

        game = new MineSweeperGame(this, 9, 9, Difficulty.EINFACH);

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
        float paneWidth = getWidth() / Constants.COLUMN_FOR_SIZE * game.getColumns();
        camera = new Camera(paneWidth,
                paneWidth * ((float)game.getRows() / game.getColumns()),
                getWidth(),
                getCutHeight());
    }

    public Handler getHandler(){
        return handler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setColor(getResources().getColor(R.color.scheme_3));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        float width = canvas.getWidth() * ((float) game.getColumns() / Constants.COLUMN_FOR_SIZE);
        float height = width * ((float) game.getRows() / game.getColumns());

        float ySet = (0.0f + getCutHeight() - height) / 2;
        if(ySet < 0)
            ySet = 0;

        float xW = width / game.getColumns();
        float yW = height / game.getRows();

        float xW10 = xW * 0.1f;
        float xW80 = xW * 0.8f;

        float yW10 = yW * 0.1f;
        float yW80 = yW * 0.8f;

        paint.setColor(Color.LTGRAY);
        canvas.drawRect(camera.getXOffset(), camera.getYOffset() + ySet, camera.getXOffset() + width, camera.getYOffset() + height + ySet, paint);

        paint.setColor(Color.GRAY);
        for(int row = 1; row < game.getRows(); row++){
            canvas.drawRect(camera.getXOffset() + 0, camera.getYOffset() + ySet + yW * row - 5, camera.getXOffset() + width, camera.getYOffset() + ySet + yW * row + 5, paint);
        }

        for(int column = 1; column < game.getColumns(); column++){
            canvas.drawRect(camera.getXOffset() + xW * column - 5, camera.getYOffset() + ySet, camera.getXOffset() + xW * column + 5, camera.getYOffset() + height + ySet, paint);
        }

        for(int row = 0; row < game.getRows(); row++){
            for(int column = 0; column < game.getColumns(); column++){
                int s = game.getState(row, column);

                float x = width / game.getColumns() * column + camera.getXOffset();
                float y = ySet + height / game.getRows() * row + camera.getYOffset();

                if(s == 0 || s == 1){
                    paint.setColor(getResources().getColor(R.color.topLeftSweeperColor));

                    Path path = new Path();
                    path.moveTo(x, y);
                    path.lineTo(x + xW, y);
                    path.lineTo(x, y + yW);
                    path.lineTo(x, y);
                    path.close();

                    canvas.drawPath(path, paint);

                    paint.setColor(getResources().getColor(R.color.bottomRightSweeperColor));

                    Path path2 = new Path();
                    path2.moveTo(x, y + yW);
                    path2.lineTo(x + xW, y + yW);
                    path2.lineTo(x + xW, y);
                    path2.lineTo(x, y + yW);
                    path2.close();

                    canvas.drawPath(path2, paint);

                    paint.setColor(getResources().getColor(R.color.mainSweeperColor));
                    canvas.drawRect(x + xW10, y + yW10, x + xW80 + xW10, y + yW80 + yW10, paint);

                    if(s == 1){
                        Drawable flagIcon = getResources().getDrawable(R.mipmap.flag_icon_foreground);
                        flagIcon.setBounds((int) (x + xW10 * 2f), (int) (y + yW10 * 2f), (int) (x + xW80), (int) (y + yW80));
                        flagIcon.draw(canvas);
                    }
                } else if(s == 2 || s == 3) {
                    if(s == 3) {
                        paint.setColor(Color.RED);
                        canvas.drawRect(x, y, x + xW, y + yW, paint);
                    }

                    Drawable flagIcon = getResources().getDrawable(R.mipmap.bomb_icon_foreground);
                    flagIcon.setBounds((int) (x + xW10 * 2f), (int) (y + yW10 * 2f), (int) (x + xW80), (int) (y + yW80));
                    flagIcon.draw(canvas);
                } else if(s == 4){
                    int v = game.getValue(row, column);
                    if(v > 0 && v <= 8){
                        Rect bounds = new Rect();
                        paint.setTypeface(Typeface.DEFAULT_BOLD);

                        paint.setTextSize(48f);
                        paint.setLinearText(true);
                        paint.setSubpixelText(true);
                        paint.getTextBounds(v + "", 0, 1, bounds);

                        paint.setColor(getContext().getResources().getIntArray(R.array.numberColor)[v - 1]);
                        paint.setTextSize(48f * (yW80 - yW10 * 2) / (float)bounds.height());
                        paint.getTextBounds(v + "", 0, 1, bounds);
                        float sizeToCenter = (((xW80 - xW10 * 2) - bounds.width()) / 2);
                        float coord = x + (xW10 * 2) + sizeToCenter;

                        float xSet = 0;
                        if(v == 1)
                            xSet-=10;

                        canvas.drawText(String.valueOf(v), coord + xSet, y + (yW80), paint);
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
        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        float width = camera.getPaneWidth();
        float height = camera.getPaneHeight();

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

        int rowIndex = (int) ((event.getY() - ySet - camera.getYOffset()) / (height / game.getRows()));
        int columnIndex = (int) ((event.getX() - camera.getXOffset()) / (width / game.getColumns()));

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
                if(game.isGameOver())
                    return true;
                if(!game.isGenerated() && !camera.isMovePointSet()){
                    game.generate(rowIndex, columnIndex);
                }
                if(downPoint != null){
                    downPoint = null;
                    game.clickField(rowIndex, columnIndex);
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

                        int newRows = 0;
                        int newColumns = 0;
                        Difficulty difficulty = Difficulty.BENUTZERDEFINIERT;
                        game.getTimeManager().clearTimer();
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
                            BenutzerdefiniertDialog bdd = new BenutzerdefiniertDialog(getContext());
                            bdd.show();
                            bdd.getSubmitButton().setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bdd.cancel();
                                    MineManager.getInstance().setMaxMines(bdd.getMineBar().getProgress() + 10);
                                    clearBoard(bdd.getRowBar().getProgress() + 9, bdd.getColumnBar().getProgress() + 9, Difficulty.BENUTZERDEFINIERT);
                                }
                            });
                            return;
                        }

                        clearBoard(newRows, newColumns, difficulty);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clearBoard(int rows, int columns, Difficulty difficulty){
        game.getTimeManager().stopTimer();
        game.getTimeManager().clearTimer();
        game = new MineSweeperGame(this, rows, columns, difficulty);

        downPoint = null;
        createCamera();

        PointManager.getInstance().resetPoints();
        MineManager.getInstance().setHitMines(0);
        invalidate();
    }

}
