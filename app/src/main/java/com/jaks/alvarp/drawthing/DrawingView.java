package com.jaks.alvarp.drawthing;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

/**
 * Created by alvaro on 10/25/14.
 */
public class DrawingView extends View {
    private float brushSize, lastBrushSize;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0XFFFF6666;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    protected Bitmap canvasBitmap;

    private int[][] colors = new int[16][16];//size of actual image to be saved.

    private DrawPixel pixelatr;//class that pixelates it.


    private int thesmallone;//smaller dimension of drawingi section.

    private boolean erase = false;

    public void setErase(boolean isErase)//self explanatory.
    {
        //somewhat self explanetory?
        erase = isErase;
        if (erase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    public DrawingView(Context context) {
        super(context);
    }

    //starts a new canvas.
    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        for (int x = 0; x < 16; x++)
        {
            for (int y = 0; y < 16; y++)
            {
                colors[x][y] = -1;

            }
        }
        invalidate();
    }


    //just drawingview.
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //read methond name.
    public void setColor(String newColor) {

        reUp();
        invalidate();//still haven't figured out what this does.
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);


    }


    private void reUp()//draws everything saved.
    {
        Paint tempPaint = new Paint();
        tempPaint.setStyle(Paint.Style.FILL);

        for (int x = 0; x < 16; x++)
        {
            for (int y = 0; y < 16; y++)
            {
                tempPaint.setColor(colors[x][y]);
                drawCanvas.drawRect(pixelatr.min(x * thesmallone / 16), pixelatr.min(y * thesmallone / 16), pixelatr.max(x * thesmallone / 16), pixelatr.max(y * thesmallone / 16), tempPaint);
                System.out.println(x + "," + y + colors[x][y]);
            }
        }
    }


    private void setupDrawing() {
        drawPaint = new Paint(Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);//ctrl+click to figure this guy out.

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(false);///low def is the best def.


        for (int x = 0; x < 16; x++)//set to size of image. Fills it with white by default.
        {
            for (int y = 0; y < 16; y++) {
                colors[x][y] = -1;

            }
        }

        drawPaint.setStrokeWidth(thesmallone / 16);//small size.
        drawPaint.setStyle(Paint.Style.FILL);//makes sure tofill those rectangles.
        drawPaint.setStrokeJoin(Paint.Join.MITER);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);//draws the rectangles.

        canvasPaint = new Paint(Paint.FILTER_BITMAP_FLAG);//the guy I care about.
        canvasPaint.setAntiAlias(false);


        brushSize = 1;//small
        lastBrushSize = brushSize;

        drawPaint.setStrokeWidth(brushSize);
        //get drawing area setup for interaction
        drawPaint.setStyle(Paint.Style.FILL);

    }

    public void setBrushSize(float newSize) {
//        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                newSize, getResources().getDisplayMetrics());
        brushSize = newSize;
        //drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }


    public float getLastBrushSize() {
        return lastBrushSize;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)//when the size of workspace changes... WandH need to be there for the override to work.
    {
        if (w > h)
        {
            thesmallone = h;
        }
        else
        {
            thesmallone = w;
        }//setting the smallone to the smaller of two widths...

        thesmallone -= thesmallone % 16;//making sure the smallone is a multiple of 16.

        canvasBitmap = Bitmap.createBitmap(thesmallone, thesmallone, Bitmap.Config.ARGB_4444);
        pixelatr = new DrawPixel(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);
        reUp();


    }

    public Bitmap getResizedBitmap()//my bitmap "resizing" function that actually just draws a bitmap from an array.
    {
        Bitmap resizedBitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_4444);
        for (int x = 0; x < 16; x++)//makes sense if you know programming.
        {
            for (int y = 0; y < 16; y++) {
                resizedBitmap.setPixel(x, y, colors[x][y]);
            }
        }
        // "RECREATE" THE NEW BITMAP
        return resizedBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {//when it's drawn, draw at 0,0. This is where I'll centralize it
        canvas.drawBitmap(canvasBitmap, (canvas.getWidth() - canvasBitmap.getScaledWidth(canvas) ) / 2, (canvas.getHeight() - canvasBitmap.getScaledHeight(canvas) ) / 2, null);//bitmap's width minus normal width. Works this way, because canvas size goes to the corner. That's my guess, anyway.
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //when touched..
        float touchX = event.getX();
        float touchY = event.getY();
        //draw the square now

        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setFilterBitmap(false);

        //starts by divinding by 16, for the sake of bmp's vereywhere. Also, probably would be better to use for drawREct. Whatever.
        int relX = (int) pixelatr.min(touchX) / (thesmallone / 16);
        int relY = (int) pixelatr.min(touchY) / (thesmallone / 16);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://1st pos
                if (relX < 16 && relX > -1 && relY > -1 && relY < 16)
                {
                    for (int x = 0; x < (int) brushSize; x++)
                    {
                        for (int y = 0; y < (int) brushSize; y++)
                        {
                            colors[relX + x][relY + y] = drawPaint.getColor();
                            if (y + relY > 14)
                            {
                                y = (int) brushSize;
                            }
                        }
                        if (x + relX > 14)
                        {
                            x = (int) brushSize;
                        }
                    }
                }
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.min(touchY + brushSize * thesmallone / 16), pixelatr.min(touchX + brushSize * thesmallone / 16), pixelatr.min(touchY), drawPaint);
                break;
            case MotionEvent.ACTION_MOVE://records the positions and directions.
                if (relX < 16 && relX > -1 && relY > -1 && relY < 16)
                {
                    for (int x = 0; x < (int) brushSize; x++)
                    {
                        for (int y = 0; y < (int) brushSize; y++)
                        {
                            colors[relX + x][relY + y] = drawPaint.getColor();
                            if (y + relY > 14)
                            {
                                y = (int) brushSize;
                            }
                        }
                        if (x + relX > 14)
                        {
                            x = (int) brushSize;
                        }

                    }
                }
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.min(touchY + brushSize * thesmallone / 16), pixelatr.min(touchX + brushSize * thesmallone / 16), pixelatr.min(touchY), drawPaint);
                break;
            case MotionEvent.ACTION_UP://places it.
                break;
            default:
                return false;
        }


        invalidate();
        return true;

    }
}
