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
    private Bitmap bit;
    private float theBigOne;

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

        bit = pic;

        theBigOne = y / 16;
    }


    public float min(float val)
    {
        float over = val % 16;
        return (float)(int)((val - over) / 16) * (16);
    }

    public float max(float val)
    {
        float ovah = 16 - (val % 16);
        return (float)(int)((val + ovah) / 16) * (16);
    }
}
