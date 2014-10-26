package com.jaks.alvarp.drawthing;

import android.graphics.Bitmap;

/**
 * Created by alvaro on 10/25/14.
    at least for now, this is going to be a lot of pseudocode that will (hopefully) be converted to people code.

 */

public class DrawPixel
{

    private Bitmap bit;
    private float theBigOne;
    private float pixelRate;

    public DrawPixel(Bitmap pic)
    {
        if(pic.getHeight() > pic.getWidth())
        {
            theBigOne = pic.getWidth();
        }
        else
        {
            theBigOne = pic.getHeight();
        }

        bit = pic;

        pixelRate = theBigOne / 16;
    }


    public float min(float val)
    {
       float returner = (float) val - (val % pixelRate );
        if(returner > theBigOne)
        {
            return theBigOne;
        }
        else if(returner < 0)
        {
            return 0;
        }
        else
        {
            return returner;
        }
    }

    public float max(float val)
    {
        float returner =(float) val + (pixelRate - val % pixelRate );
        if(returner > theBigOne - pixelRate)
        {
            return theBigOne - pixelRate;
        }
        else if(returner < 0)
        {
            return 0;
        }
        else
        {
            return returner;
        }
    }
}
