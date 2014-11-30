package com.jaks.alvarp.drawthing.utils;

import android.graphics.Bitmap;
import android.graphics.Color;


import java.util.Random;

/**
 * Created by alvaro on 11/30/14.
 */
public class ImageManipulator {
    Random randy = new Random();
    Color color = new Color();



    public void addNoise(Bitmap toMess, int level)
    {
        for(int x = 0; x < toMess.getWidth(); x++)
        {
            for(int y = 0; y < toMess.getHeight(); y++)
            {
                int r = randomize(Color.red(toMess.getPixel(x, y)), level);
                int g = randomize(Color.green(toMess.getPixel(x, y)), level);
                int b = randomize(Color.blue(toMess.getPixel(x, y)), level);

                int col = Color.rgb(r, g, b);
                toMess.setPixel(x, y, col);






            }
        }
    }

    private int randomize(int init, int level) {
        int returned = init + level - randy.nextInt(level);
        if (returned > 255) {
            return 255;
        } else if (returned < 0) {
            return 0;
        }
        return returned;

    }
}
