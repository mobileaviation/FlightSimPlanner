package nl.robenanita.googlemapstest.Instruments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 26-8-2014.
 */
public class AltimeterView extends View {
    public AltimeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        big = false;
        this.context = context;
        height = 0;
        setupView(big);
    }

    Context context;
    boolean big;
    float height;
    int am, os, ap1, ap2;
    Bitmap altimeterBitmap;
    Bitmap outsideBitmap;
    Bitmap pointerLowBitmap;
    Bitmap pointerHighBitmap;

    private void setupView(boolean big)
    {
        Resources res = context.getResources();

        if (big)
        {
            am = R.drawable.altimeterbig;
            os = R.drawable.altimeteroutsidebig;
            ap1 = R.drawable.altpointer1big;
            ap2 = R.drawable.altpointer2big;
        }
        else
        {
            am = R.drawable.altimeter;
            os = R.drawable.altimeteroutside;
            ap1 = R.drawable.altpointer1;
            ap2 = R.drawable.altpointer2;
        }

        altimeterBitmap = BitmapFactory.decodeResource(res, am);
        outsideBitmap = BitmapFactory.decodeResource(res, os);
        pointerLowBitmap = BitmapFactory.decodeResource(res, ap1);
        pointerHighBitmap = BitmapFactory.decodeResource(res, ap2);
    }

    public void setHeight(float height)
    {
        this.height = height;
        invalidate();
        requestLayout();
    }
    public void setBig(boolean big)
    {
        this.big = big;
        setupView(big);
        invalidate();
        requestLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        double angleLow = ((double)height/1000d)*360;
        double angleHigh = ((double)height/10000d)*360;
        // Create blank bitmap of equal size
        Bitmap rotateLowBitmap = Bitmap.createBitmap(pointerLowBitmap.getWidth(), pointerLowBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas pointerLowCanvas = new Canvas(rotateLowBitmap);
        Bitmap rotateHighBitmap = Bitmap.createBitmap(pointerHighBitmap.getWidth(), pointerHighBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas pointerHighCanvas = new Canvas(rotateHighBitmap);
        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate((float)angleLow, pointerLowBitmap.getWidth()/2, pointerLowBitmap.getHeight()/2);
        pointerLowCanvas.drawBitmap(pointerLowBitmap, rotateMatrix, null);
        rotateMatrix.setRotate((float)angleHigh, pointerHighBitmap.getWidth()/2, pointerHighBitmap.getHeight()/2);
        pointerHighCanvas.drawBitmap(pointerHighBitmap, rotateMatrix, null);

        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(outsideBitmap, 0, 0, null);
        canvas.drawBitmap(altimeterBitmap, (float)midx-(altimeterBitmap.getWidth()/2), (float)midy-(altimeterBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateHighBitmap, (float)midx-(rotateHighBitmap.getWidth()/2), (float)midy-(rotateHighBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateLowBitmap, (float)midx-(rotateLowBitmap.getWidth()/2), (float)midy-(rotateLowBitmap.getHeight()/2), null);

    }
}
