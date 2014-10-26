package com.jaks.alvarp.drawthing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.util.UUID;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class DrawScreen extends Activity implements OnClickListener {
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, tempBtn;
    private Button rgbBtn;
    private EditText RGBvalues;
    private String value = "";

    public void setRGB(View view) {
        RGBvalues = (EditText) findViewById(R.id.rgb_values);
        value = (RGBvalues.getText().toString());
        paintClicked(view);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
    }


    public void paintClicked(View view) {
        if (view != currPaint) {
            ImageButton imgView;
            String color;
            if (value.length() == 7) {
                drawView.setColor(value);
                value = "";
                currPaint = tempBtn;
                drawView.setErase(false);
            } else {
                color = view.getTag().toString();
                imgView = (ImageButton) view;
                drawView.setColor(color);
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint = (ImageButton) view;
                drawView.setErase(false);
            }


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

        rgbBtn = (Button) findViewById(R.id.RGBbutton);
        rgbBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
//                       Log.v("rgbText", rgbText.getText().toString());
                        setRGB(view);
                    }
                });

        tempBtn = (ImageButton) findViewById(R.id.temp_value);
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
        } else if (view.getId() == R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;


                    //Bitmap aBmp = Bitmap.createScaledBitmap(drawView.canvasBitmap, 16, 16, false);

                    Bitmap aBmp = getResizedBitmap(drawView.canvasBitmap, 16, 16);

                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), aBmp,
                            UUID.randomUUID().toString() + ".png", "drawing");
                    if (imgSaved != null) {
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    } else {
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
