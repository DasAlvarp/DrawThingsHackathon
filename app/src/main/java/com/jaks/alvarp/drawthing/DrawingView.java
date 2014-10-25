package com.jaks.alvarp.drawthing;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by alvaro on 10/25/14.
 */
public class DrawingView extends View
{
    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }
    private void setupDrawing(){
        //get drawing area setup for interaction
    }
}
