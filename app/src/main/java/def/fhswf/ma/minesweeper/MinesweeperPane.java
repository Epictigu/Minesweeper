package def.fhswf.ma.minesweeper;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MinesweeperPane extends View {

    private Random random = new Random();

    private int rows = 9;
    private int columns = 9;

    private int[][] state;
    private int[][] value = null;

    private Map<Integer, Integer> numberColors = new HashMap<Integer, Integer>();

    private Point downPoint = null;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(downPoint != null){
                float width = getWidth();
                float height = width * (rows / columns);

                float ySet = (0.0f + getHeight() - height) / 2;

                int rowIndex = (int) ((((float)downPoint.y) - ySet) / (height / rows));
                int columnIndex = (int) (((float)downPoint.x) / (width / columns));

                if(state[rowIndex][columnIndex] == 0)
                    state[rowIndex][columnIndex] = 1;
                else if(state[rowIndex][columnIndex] == 1)
                    state[rowIndex][columnIndex] = 0;
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

        setLongClickable(true);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                System.out.println("Test");



                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setColor(getResources().getColor(R.color.scheme_3));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        float width = canvas.getWidth();
        float height = width * (rows / columns);

        float ySet = (0.0f + canvas.getHeight() - height) / 2;

        float xW = width / columns ;
        float yW = height / rows;

        float xW10 = xW * 0.1f;
        float xW80 = xW * 0.8f;

        float yW10 = yW * 0.1f;
        float yW80 = yW * 0.8f;

        paint.setColor(Color.LTGRAY);
        canvas.drawRect(0, 0 + ySet, width, height + ySet, paint);

        paint.setColor(Color.GRAY);
        for(int row = 1; row < 9; row++){
            canvas.drawRect(0, ySet + yW * row - 5, width, ySet + yW * row + 5, paint);
        }

        for(int column = 1; column < 9; column++){
            canvas.drawRect(xW * column - 5, ySet, xW * column + 5, height + ySet, paint);
        }

        for(int row = 0; row < 9; row++){
            for(int column = 0; column < 9; column++){
                int s = state[row][column];

                float x = width / columns * column;
                float y = ySet + height / rows * row;

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
                    if(s == 2)
                        paint.setColor(Color.WHITE);
                    else if(s == 3)
                        paint.setColor(Color.RED);
                    canvas.drawRect(x, y, x + xW, y + yW, paint);

                    Drawable flagIcon = getResources().getDrawable(R.mipmap.bomb_icon_foreground);
                    flagIcon.setBounds((int) (x + xW10 * 2f), (int) (y + yW10 * 2f), (int) (x + xW80), (int) (y + yW80));
                    flagIcon.draw(canvas);
                } else if(s == 4){
                    paint.setColor(Color.LTGRAY);
                    //canvas.drawRect(x, y, x + xW, y + yW, paint);

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
                        System.out.println(xW + " " + yW);
                        System.out.println(v + " " + bounds.width() + " " + (xW80 - xW10 * 2) + " " + (((xW80 - xW10 * 2) - bounds.width()) / 2));
                        float sizeToCenter = (((xW80 - xW10 * 2) - bounds.width()) / 2);
                        float coord = x + (xW10 * 2) + sizeToCenter;

                        float xSet = 0;
                        if(v == 1)
                            xSet-=10;

                        canvas.drawText(String.valueOf(v), coord + xSet, y+ (yW80), paint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        int x = (int) event.getX();
        int y = (int) event.getY();

        float width = getWidth();
        float height = width * (rows / columns);

        float ySet = (0.0f + getHeight() - height) / 2;

        if(y < ySet || y > ySet + height)
            return true;

        int rowIndex = (int) ((event.getY() - ySet) / (height / rows));
        int columnIndex = (int) (event.getX() / (width / columns));

        Point pressPoint = new Point(x, y);

        switch(action){
            case MotionEvent.ACTION_DOWN:
                if(downPoint != null){
                    handler.removeCallbacks(runnable);
                }
                downPoint = pressPoint;
                handler.postDelayed(runnable, 400);
                break;
            case MotionEvent.ACTION_MOVE:
                if(downPoint != null)
                    if(getDistance(pressPoint, downPoint) > 5) {
                        downPoint = null;
                        handler.removeCallbacks(runnable);
                    }

                break;
            case MotionEvent.ACTION_UP:
                if(downPoint != null){
                    downPoint = null;
                    if(value == null){
                        generate(rowIndex, columnIndex);
                    }
                    clickField(rowIndex, columnIndex);
                    handler.removeCallbacks(runnable);
                    invalidate();
                }
                break;
        }

        return true;
    }

    private void clickField(int row, int column){
        int v = value[row][column];
        if(v == -1) {
            state[row][column] = 3;
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
        } else if(v > 0 && v < 9){
            state[row][column] = 4;
        }
    }

    private Double getDistance(Point p1, Point p2){
        float xDistance = p2.x - p1.x;
        float yDistance = p2.y - p1.y;
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    private void generate(int rowSafe, int columnSafe){
        value = new int[rows][columns];

        List<Point> availablePoints = new ArrayList<Point>();
        int x = 0, y = 0;
        for(int[] i : value){
            y = 0;
            Arrays.fill(i, 0);
            for(int j : i){
                if(!(x == rowSafe && y == rowSafe))
                    availablePoints.add(new Point(x, y));
                y++;
            }
            x++;
        }

        for(int i = 0; i < 10; i++){
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

    }

}
