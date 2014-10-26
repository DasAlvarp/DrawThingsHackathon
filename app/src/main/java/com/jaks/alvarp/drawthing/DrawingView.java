package com.jaks.alvarp.drawthing;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
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
    private Bitmap canvasBitmap;

    private DrawPixel pixelatr;


    /*private EditText RGBvalues = (EditText) findViewById(R.id.rgb_values);
    public String value = ("#" + RGBvalues.getText().toString());
    public int parseColor = Integer.parseInt(value);
    */

    protected float thesmallone;

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
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(false);




        drawPaint.setStrokeWidth(thesmallone / 16);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.MITER);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);

        canvasPaint = new Paint(Paint.DITHER_FLAG);




        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPaint.setStrokeWidth(brushSize);
        //get drawing area setup for interaction
        drawPaint.setStyle(Paint.Style.FILL);

    }

    public void setBrushSize(float newSize)
    {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
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
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        int thesmallone;
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

        canvasBitmap = Bitmap.createBitmap(thesmallone, thesmallone, Bitmap.Config.ARGB_8888);
        pixelatr = new DrawPixel(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);


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


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN://1st pos
                break;
            case MotionEvent.ACTION_MOVE://records the positions and directions.
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.max(touchY), pixelatr.max(touchX),pixelatr.min(touchY), drawPaint);
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
