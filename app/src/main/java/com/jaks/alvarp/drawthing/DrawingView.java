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

    protected int[][] colors = new int[16][16];//size of actual image to be saved.

    private DrawPixel pixelatr;//class that pixelates it.




    protected int thesmallone;//smaller dimension of drawingi section.

    private boolean erase=false;

    public void setErase(boolean isErase)//self explanatory.
    {
        //somewhat self explanetory?
        erase = isErase;
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

    //starts a new canvas.
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


    //just drawingview.
    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setupDrawing();
    }

//read methond name.
    public void setColor(String newColor)
    {
        invalidate();//still haven't figured out what this does.
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);

    }

    private void setupDrawing()
    {
        drawPaint = new Paint(Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);//ctrl+click to figure this guy out.

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(false);///low def is the best def.


        for(int x = 0; x < 16; x++)//set to size of image. Fills it with white by default.
        {
            for(int y = 0; y < 16; y++)
            {
                colors[x][y] = -1;

            }
        }

        drawPaint.setStrokeWidth(thesmallone / 16);//small size.
        drawPaint.setStyle(Paint.Style.FILL);//makes sure tofill those rectangles.
        drawPaint.setStrokeJoin(Paint.Join.MITER);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);//draws the rectangles.

        canvasPaint = new Paint(Paint.FILTER_BITMAP_FLAG );//the guy I care about.
        canvasPaint.setAntiAlias(false);




        brushSize = 1;//small
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh)//when the size of workspace changes...
    {
        super.onSizeChanged(w, h, oldw, oldh);//execute superclass method.
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);///creawtes a bitmap with this stuff...

        int x = canvasBitmap.getWidth();//gets these things. PRobably would be better to have super.whatever for that stuff.
        int y = canvasBitmap.getHeight();//but I'm not programming right now.
        if(x > y)
        {
            thesmallone = y;
        }
        else
        {
            thesmallone = x;
        }//setting the smallone to the smaller of two widths...

        thesmallone -= thesmallone % 16;//making sure the smallone is a multiple of 16.

        canvasBitmap = Bitmap.createBitmap(thesmallone, thesmallone, Bitmap.Config.ARGB_8888);
        pixelatr = new DrawPixel(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);


    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)////oldgetresizdbitmap method. Doesn't do antyhhgin. WIll delete soon.
    {
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
    protected void onDraw(Canvas canvas) {//when it's drawn, draw at 0,0. This is where I'll centralize it.

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //when touched..
        float touchX = event.getX();
        float touchY = event.getY();
        //draw the square now

        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setFilterBitmap(false);

        //starts by divinding by 16, for the sake of bmp's vereywhere. Also, probably would be better to use for drawREct. Whatever.
        int relX = (int)pixelatr.min(touchX) / (thesmallone / 16);
        int relY = (int)pixelatr.min(touchY) / (thesmallone / 16);


        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN://1st pos
                if(relX < 16 && relX > -1 && relY > -1 && relY < 16)
                    colors[relX][relY] = drawPaint.getColor();
                drawCanvas.drawRect(pixelatr.min(touchX), pixelatr.min(touchY + brushSize * thesmallone / 16), pixelatr.min(touchX + brushSize * thesmallone / 16),pixelatr.min(touchY), drawPaint);
                break;
            case MotionEvent.ACTION_MOVE://records the positions and directions.
                if(relX < 16 && relX > -1 && relY > -1 && relY < 16)
                    colors[relX][relY] = drawPaint.getColor();
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
