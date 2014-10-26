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
public class DrawingView extends View
{
    private float brushSize, lastBrushSize;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    protected Bitmap canvasBitmap;

    protected int[][] colors = new int[16][16];

    private DrawPixel pixelatr;




    protected int thesmallone;

    private boolean erase=false;

    public void setErase(boolean isErase)
    {
        erase=isErase;
        if(erase)
        {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else
        {
            drawPaint.setXfermode(null);
        }
    }

    public DrawingView(Context context)
    {
        super(context);
    }

    public void startNew()
    {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        for(int x = 0; x < 16; x++)
        {
            for(int y = 0; y < 16; y++)
            {
                colors[x][y] = -1;

            }
        }
        invalidate();
    }

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setupDrawing();
    }


    public void setColor(String newColor)
    {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);

    }

    private void setupDrawing()
    {
        drawPaint = new Paint(Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(false);


        for(int x = 0; x < 16; x++)
        {
            for(int y = 0; y < 16; y++)
            {
                colors[x][y] = -1;

            }
        }

        drawPaint.setStrokeWidth(thesmallone / 16);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.MITER);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);

        canvasPaint = new Paint(Paint.FILTER_BITMAP_FLAG );
        canvasPaint.setAntiAlias(false);




        brushSize = 1;
        lastBrushSize = brushSize;

        drawPaint.setStrokeWidth(brushSize);
        //get drawing area setup for interaction
        drawPaint.setStyle(Paint.Style.FILL);

    }

    public void setBrushSize(float newSize)
    {
//        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                newSize, getResources().getDisplayMetrics());
        brushSize = newSize;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize)
    {
        lastBrushSize=lastSize;
    }



    public float getLastBrushSize()
    {
        return lastBrushSize;
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);

        int x = canvasBitmap.getWidth();
        int y = canvasBitmap.getHeight();
        if(x > y)
        {
            thesmallone = y;
        }
        else
        {
            thesmallone = x;
        }

        thesmallone -= thesmallone % 16;

        canvasBitmap = Bitmap.createBitmap(thesmallone, thesmallone, Bitmap.Config.ARGB_8888);
        pixelatr = new DrawPixel(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);


    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();
        //draw the square now

        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setFilterBitmap(false);


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN://1st pos
                colors[(int)pixelatr.min(touchX) / (thesmallone / 16)][(int)pixelatr.min(touchY) / (thesmallone / 16)] = drawPaint.getColor();
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.min(touchY + brushSize * thesmallone / 16), pixelatr.min(touchX + brushSize * thesmallone / 16),pixelatr.min(touchY), drawPaint);
                break;
            case MotionEvent.ACTION_MOVE://records the positions and directions.
                colors[(int)pixelatr.min(touchX) / (thesmallone / 16)][(int)pixelatr.min(touchY) / (thesmallone / 16)] = drawPaint.getColor();
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.min(touchY + brushSize * thesmallone / 16), pixelatr.min(touchX + brushSize * thesmallone / 16),pixelatr.min(touchY), drawPaint);
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
