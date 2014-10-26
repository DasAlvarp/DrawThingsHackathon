package com.jaks.alvarp.drawthing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class DrawScreen extends Activity implements OnClickListener
{
    private float smallBrush, mediumBrush, largeBrush;
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;







    public void paintClicked(View view){
        if(view != currPaint)
        {
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            if (drawView.value.length() == 6)
            {
                drawView.setColor(drawView.value);
            }
            else
            {
                drawView.setColor(color);
            }

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton)view;
            drawView.setErase(false);
            drawView.setBrushSize(drawView.getLastBrushSize());

        }



    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {//creates all the UI elements, etc...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_screen);
        drawView = (DrawingView)findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);



        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);


        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.draw_btn){
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("How big you tryna go:");
            brushDialog.setContentView(R.layout.brush_chooser);



            //Setting up buttons to change sizes, etc...
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setBrushSize(drawView.theSmallOne / 16);
                    drawView.setLastBrushSize(drawView.theSmallOne / 16);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setBrushSize(drawView.theSmallOne / 12);
                    drawView.setLastBrushSize(drawView.theSmallOne / 12);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setBrushSize(drawView.theSmallOne / 8);
                    drawView.setLastBrushSize(drawView.theSmallOne / 8);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();

        }
        else if(view.getId()==R.id.erase_btn)
        {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Delete ya mistake:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setErase(true);
                    drawView.setBrushSize(drawView.theSmallOne / 16);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setErase(true);
                    drawView.setBrushSize(drawView.theSmallOne / 12);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    drawView.setErase(true);
                    drawView.setBrushSize(drawView.theSmallOne / 8);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        }
        else if(view.getId()==R.id.new_btn)
        {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Pixlatr canvas");
            newDialog.setMessage("Start new drawing (finna lose yo current drawing)?");
            newDialog.setPositiveButton("Hell Yeah", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Nah, dude.", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save this thang");
            saveDialog.setMessage("Tryna save your dope ass picture to yo Gallery?");
            saveDialog.setPositiveButton("Hell yeah!", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "That shit was saved!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else
                    {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Yo shit can't be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Nah, dude.", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();

                }
            });
            saveDialog.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.draw_screen, menu);
        drawView.setBrushSize(drawView.theSmallOne / 12);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
