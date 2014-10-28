package com.jaks.alvarp.drawthing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jaks.alvarp.drawthing.fileWorkarounds.ImageWorkaround;


public class DrawScreen extends Activity implements OnClickListener {
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    private EditText RGBvalues;
    private String value = "";


    public void setRGB(View view) {
        RGBvalues = (EditText) findViewById(R.id.rgb_values);
        value = (RGBvalues.getText().toString());
        rgbClicked(view);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
    }

    public void rgbClicked(View view)
    {
        try{
            drawView.setColor(value);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
    }


    public void paintClicked(View view) {
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            drawView.setColor(view.getTag().toString());
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
            drawView.setErase(false);
            drawView.setBrushSize(drawView.getLastBrushSize());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {//creates all the UI elements, etc...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_screen);
        drawView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);


        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);


        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        RGBvalues = (EditText) findViewById(R.id.rgb_values);
        RGBvalues.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    setRGB(v);
                return false;
            }
        });



    }

    public Bitmap getResizedBitmap(int[][] bm)//my bitmap "resizing" function that actually just draws a bitmap from an array.
    {
        Bitmap resizedBitmap = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_4444);
        for(int x = 0; x < 16; x++)//makes sense if you know programming.
        {
            for(int y = 0; y < 16; y++)
            {

                resizedBitmap.setPixel(x, y, bm[x][y]);
            }
        }
        // "RECREATE" THE NEW BITMAP
        return resizedBitmap;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("How big you tryna go:");
            brushDialog.setContentView(R.layout.brush_chooser);


            //Setting up buttons to change sizes, etc...
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(1);
                    drawView.setLastBrushSize(1);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(2);
                    drawView.setLastBrushSize(2);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(3);
                    drawView.setLastBrushSize(3);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();

        } else if (view.getId() == R.id.erase_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Delete ya mistake:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(1);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(2);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(3);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (view.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if (view.getId() == R.id.save_btn) {//my esteemed save function.
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);//say shitl
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);//saves previeous drawing? Remembers it? My guesses.

                    BitmapFactory.Options options = new BitmapFactory.Options();//stuff from internet.
                    options.inScaled = false;//tried to turn of inside scaling.


                    //Bitmap aBmp = Bitmap.createScaledBitmap(drawView.canvasBitmap, 16, 16, false);


                    Bitmap aBmp = getResizedBitmap(drawView.colors);//got bitmap for this. DOn't think I need the options earlier.


                    /*dysfuntional more conventional java filestram/saving code. Libraries are considered
                    'read only' using this method, so it doesn't work, at least that's my guess...
                    FileOutputStream out = null;
                    try
                    {
                        out = new FileOutputStream("yerpic");
                        aBmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/

                    String imgSaved = com.jaks.alvarp.drawthing.fileWorkarounds.ImageWorkaround.Media.addImage(
                            getContentResolver(), aBmp, UUID.randomUUID().toString() + ".png", "drawing");
                    ///the MediaStore.Images.Media.insertImage() function is the one I'm thinking about
                    //overriding, by copying a good section of the entire class. MOst of it uses public methods, so
                    //this should be somewhat doable, but time consuming, especially for editing one line of code.

                    if (imgSaved != null)
                    {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else
                    {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }


                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            saveDialog.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.draw_screen, menu);
        drawView.setBrushSize(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
