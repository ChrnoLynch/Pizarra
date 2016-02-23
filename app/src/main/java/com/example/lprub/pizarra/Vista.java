package com.example.lprub.pizarra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Fran on 15/02/2016.
 */
public class Vista extends View {

    private float x0, y0, xi, yi;
    private Path rectaPoligonal = new Path();
    private Bitmap mapaDeBits;
    private Canvas lienzoFondo;
    private Paint pincel, canvasPincel;
    private int color = Color.BLUE;
    private String forma = "poligonal";
    private float tamPincel, ultTamPincel;
    private int w, h;
    private Bitmap icon;

    public Vista(Context context, AttributeSet attrs) {
        super(context, attrs);
        tamPincel = getResources().getInteger(R.integer.medium_size);
        ultTamPincel = tamPincel;
        pincel = new Paint();
        pincel.setColor(color);
        pincel.setStrokeWidth(tamPincel);
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeJoin(Paint.Join.ROUND);
        pincel.setStrokeCap(Paint.Cap.ROUND);
        canvasPincel = new Paint(Paint.DITHER_FLAG);
        icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.mustache);
    }

    @Override
    public void onDraw(Canvas lienzo) {
        lienzo.drawBitmap(mapaDeBits, 0, 0, canvasPincel);
        switch (forma) {
            case "recta":
                pincel.setStyle(Paint.Style.STROKE);
                lienzo.drawLine(x0, y0, xi, yi, pincel);
                break;
            case "poligonal":
                pincel.setStyle(Paint.Style.STROKE);
                lienzo.drawPath(rectaPoligonal, pincel);
                break;
            case "circulo":
                pincel.setStyle(Paint.Style.FILL);
                lienzo.drawCircle(
                        x0, y0,
                        (float) Math.sqrt(Math.pow(x0 - xi, 2) + Math.pow(y0 - yi, 2)),
                        pincel);
                break;
            case "cuadrado":
                pincel.setStyle(Paint.Style.FILL);
                lienzo.drawRect(x0, y0, xi, yi, pincel);
                break;
            case "bigote":
                lienzo.drawBitmap(icon, xi-64, yi-64, pincel);
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        mapaDeBits = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int puntos = event.getPointerCount();
        for (int i = 0; i < puntos; i++) {
            int accion = event.getActionMasked();
            int indicePuntero = event.getActionIndex();
            int puntero=event.getPointerId(i);
            System.out.println("ID DEL PUNTERO"+puntero);
            float x = event.getX(i);
            float y = event.getY(i);
            switch (accion) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    x0 = xi = event.getX(i);
                    y0 = yi = event.getY(i);
                    if (getForma().equals("poligonal")) {
                        rectaPoligonal.moveTo(x, y);
                        }
                    break;
                case MotionEvent.ACTION_MOVE:
                        xi = x;
                        yi = y;
                        if (getForma().equals("poligonal")) {
                            rectaPoligonal.lineTo(x, y);
                        }
                        invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                        xi = x;
                        yi = y;
                        if (getForma().equals("poligonal")) {
                            rectaPoligonal.lineTo(x, y);
                            dibujarFigura(getForma());
                            rectaPoligonal.reset();
                        } else {
                            dibujarFigura(getForma());
                        }
                    break;
            }
        }
            return true;
        }

    public void dibujarFigura(String forma) {
        switch (forma) {
            case "recta":
                pincel.setStyle(Paint.Style.STROKE);
                lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                break;
            case "poligonal":
                pincel.setStyle(Paint.Style.STROKE);
                lienzoFondo.drawPath(rectaPoligonal, pincel);
                break;
            case "circulo":
                pincel.setStyle(Paint.Style.FILL);
                lienzoFondo.drawCircle(x0, y0, (float) Math.sqrt(Math.pow(x0 - xi, 2) + Math.pow(y0 - yi, 2)),
                        pincel);
                break;
            case "cuadrado":
                pincel.setStyle(Paint.Style.FILL);
                lienzoFondo.drawRect(x0, y0, xi, yi, pincel);
                break;
            case "bigote":
                lienzoFondo.drawBitmap(icon, (xi-64), (yi-64), pincel);
        }
    }

    public void borraPantalla() {
        lienzoFondo.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String f) {
        cancelBorrar();
        this.forma = f;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        pincel.setColor(color);
    }

    public void setBorrar() {
        pincel.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void cancelBorrar() {
        pincel.setXfermode(null);
    }

    public Bitmap getMapaDeBits() {
        return mapaDeBits;
    }

    public void setMapaDeBits(Bitmap mapaDeBits) {
        borraPantalla();
        lienzoFondo.drawBitmap(mapaDeBits,getLeft(),getTop(),pincel);
    }
}