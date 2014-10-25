package com.jaks.alvarp.drawthing;

import android.graphics.Bitmap;

/**
 * Created by alvaro on 10/25/14.
    at least for now, this is going to be a lot of pseudocode that will (hopefully) be converted to people code.

 */

public class DrawPixel
{

    private float x;
    private float y;

    public DrawPixel(Bitmap pic)
    {
        if(pic.getHeight() > pic.getWidth())
        {
            x = pic.getWidth();
            y = x;
        }
        else
        {
            y = pic.getHeight();
            y = x;
        }
    }


    public void drawLine(float tx, float ty)
    {

    }
}
