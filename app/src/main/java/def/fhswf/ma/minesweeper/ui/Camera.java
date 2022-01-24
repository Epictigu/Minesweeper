package def.fhswf.ma.minesweeper.ui;

import android.graphics.Point;

public class Camera {

    private Point movePoint = null;

    private float xOffset = 0;
    private float yOffset = 0;

    private float paneWidth;
    private float paneHeight;
    private float screenWidth;
    private float screenHeight;

    public Camera(float paneWidth, float paneHeight, float screenWidth, float screenHeight){
        this.paneWidth = paneWidth;
        this.paneHeight = paneHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setMovePoint(Point movePoint){
        this.movePoint = movePoint;
    }

    public boolean isMovePointSet(){
        return movePoint != null;
    }

    public float getXOffset(){
        return xOffset;
    }

    public float getYOffset(){
        return yOffset;
    }

    public float getPaneWidth(){
        return paneWidth;
    }

    public float getPaneHeight(){
        return paneHeight;
    }

    public void move(int x, int y){
        if(movePoint == null)
            System.err.println("[FEHLER] Kamera wurde versucht ohne Bewegungspunkt zu verschieben.");

        if(paneWidth < screenWidth) {
            xOffset = 0;
        } else {
            xOffset = xOffset - (movePoint.x - x);
            if(xOffset > 0)
                xOffset = 0;
            if(xOffset < (paneWidth - screenWidth) * -1)
                xOffset = (paneWidth - screenWidth) * -1;
        }

        if(paneHeight < screenHeight) {
            yOffset = 0;
        } else {
            yOffset = yOffset - (movePoint.y - y);
            if(yOffset > 0)
                yOffset = 0;
            if(yOffset < (paneHeight - screenHeight) * -1)
                yOffset = (paneHeight - screenHeight) * -1;
        }
        movePoint = new Point(x, y);
    }

}
