package com.example.lab_streams_matveev493;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {



   String blur;
   SurfaceView sv;
   int n;
   int k = 3;
   double s = 5;
   double[][] m;
   double sum;
   int interrup = 0;
   Thread tr;
   Bitmap res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        sv = findViewById(R.id.surfaceViewImage);
        Drawable d = new BitmapDrawable(getResources(), image);
        sv.setForeground(d);



        blur = "3";
        SeekBar seekBar = findViewById(R.id.seekBarBlur);
        TextView textView = findViewById(R.id.textViewCount);



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                blur = String.valueOf(progress);
                textView.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    class Worker implements Runnable {
        TextView time = findViewById(R.id.textViewTime);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS z");
        Date date1 = new Date(System.currentTimeMillis());
        RadioButton rbb = findViewById(R.id.radioButtonBox);
        RadioButton rbg = findViewById(R.id.radioButtonGaussian);

        public int y0;
        public int y1;

        public int w;
        public int h;
        double sRed, sGreen, sBlue;


        public double[][] m;
        public double sum;
        public Bitmap bmp;
        public Bitmap res;


        @Override
        public void run() {


            Runnable run1 = new Runnable() {
                @Override
                public void run() {




                    for (int y = y0; y < y1; y++) {
                        for (int x = 0; x < w; x++) {
                            double a = 1;
                            int red = 0;
                            int green = 0;
                            int blue = 0;
                            sRed = 0;
                            sGreen = 0;
                            sBlue = 0;
                            for (int v = 0; v < k; v++)
                                for (int u = 0; u < k; u++) {
                                    int px = u + x - k / 2;
                                    int py = v + y - k / 2;

                                    if (px < 0) px = 0;
                                    if (py < 0) py = 0;
                                    if (px >= w) px = w - 1;
                                    if (py >= h) py = h - 1;
                                    int c = bmp.getPixel(px, py);
                                    if(rbg.isChecked() == true)
                                    {
                                        sRed += (double) Color.red(c) * m[u][v];
                                        sGreen += (double) Color.green(c) * m[u][v];
                                        sBlue += (double) Color.blue(c) * m[u][v];
                                    }
                                    if(rbb.isChecked() == true) {

                                        red += Color.red(c);
                                        green += Color.green(c);
                                        blue += Color.blue(c);
                                    }
                                }

                            if (interrup == 1)
                            {
                                if(rbg.isChecked() == true)
                                {
                                    sRed /= sum;
                                    sGreen /= sum;
                                    sBlue /= sum;

                                    red = (int)sRed;
                                    green = (int)sGreen;
                                    blue = (int)sBlue;
                                }
                                if(rbb.isChecked() == true) {

                                    red /= k * k;
                                    green /= k * k;
                                    blue /= k * k;
                                }
                                res.setPixel(x, y, Color.rgb((int)red, (int)green, (int)blue));
                            }


                        }


                    }


                    Date date2 = new Date(System.currentTimeMillis());
                    Long milliseconds = date2.getTime() - date1.getTime();
                    Double seconds = (double) (milliseconds / 1000);
                    DecimalFormat format = new DecimalFormat("#.00");
                    time.setText(String.valueOf(format.format(seconds)));


                }
            };
            tr = new Thread(run1);
            tr.start();
        }


    }

    double sumM(double[][] m)
    {
        double sum = 0;
        for (int i = 0; i < m.length; i++)
        {
            for (int j = 0; j < m.length; j++)
            {
                sum += m[i][j];
            }
        }
        return sum;
    }
    double[][] fillM()
    {
        double[][] m = new double[k][k];
        double e;
        double g1;
        double x, y;
        int median = k / 2;
        for (int i = 0; i < k; i++)
        {
            for (int j = 0; j < k; j++)
            {

                    g1 = 1.0D / (2 * Math.PI * (s*s));
                    x = Math.pow((i-median), 2);
                    y = Math.pow((j-median), 2);
                    e = Math.pow(Math.E, ((x + y) / (2 * (s*s))) / -1.0D);
                    m[i][j] = g1 * e;
            }
        }
        return m;
    }
        public void function(View v) {


            interrup = 1;

            k = Integer.parseInt(blur);
            m = fillM();
            sum = sumM(m);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
            int h = bmp.getHeight();
            int w = bmp.getWidth();
            res = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Spinner spinner = findViewById(R.id.spinnerType);
            n = Integer.parseInt(spinner.getSelectedItem().toString());
            Thread[] t = new Thread[n];
            Worker[] r = new Worker[n];



                        int s = h / t.length;


                        for (int i = 0; i < n; i++) {
                            r[i] = new Worker();
                            r[i].bmp = bmp;
                            r[i].res = res;
                            r[i].m = m;
                            r[i].w = w;
                            r[i].h = h;
                            r[i].sum = sum;
                            r[i].y0 = s * i;
                            r[i].y1 = r[i].y0 + s;
                            t[i] = new Thread(r[i]);
                            t[i].run();
                        }

                        for (int i = 0; i < n; i++) {
                            try {
                                t[i].join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(interrup == 1)
                        {
                            Drawable d = new BitmapDrawable(getResources(), res);
                            sv.setForeground(d);
                        }









        }
        public void Stop(View v)
        {
            try {
                tr.interrupt();
                interrup = 0;
                Drawable d = new BitmapDrawable(getResources(), res);
                sv.setForeground(d);
            }
            catch (Exception e){}
        }
    }
