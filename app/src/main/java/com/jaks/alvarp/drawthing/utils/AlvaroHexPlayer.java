package com.jaks.alvarp.drawthing.utils;

import java.util.Random;

/**
 * Created by alvaro on 10/25/14.
 */
public class AlvaroHexPlayer
{
    Random randy = new Random();

    public int hex2int(String num)
    {
        int tot = 0;
        for(int x = 0; x < num.length(); x++)
        {
            tot += hex2int(num.charAt(x)) * (x + 1);
        }
        return tot;
    }



    public String int2hex(int num)
    {
        String tot = "";
        for(int x = 0; x < num / 16; x ++)
        {
            tot += "F";
        }

        tot += "" + intTohHex(num % 16);

        return tot;
    }


    public char intTohHex(int digit)
    {
        switch (digit)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                String temp = "" + digit;
                return temp.charAt(0);
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
                return 'F';
        }
        return 'G';//should never hapen.
    }

    public int hex2int(char digit)
    {
        switch(digit){
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;


        }
        return 16;//should never happen.
    }


    public String randomizer(String num)
    {
        int x = hex2int(num);
        x = randy.nextInt(x);
        return int2hex(x);
    }
}
