package nl.robenanita.googlemapstest.Instruments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.Resources;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 28-8-2014.
 */
public class VerticalSpeedIndicatorView extends View {
    public VerticalSpeedIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        big = false;
        speed = 0;
        setupView(big);
    }

    float speed;
    Context context;
    boolean big;

    int v, os, vp;
    Bitmap vsiBitmap;
    Bitmap outsideBitmap;
    Bitmap pointerBitmap;

    private void setupView(boolean big)
    {
        Resources res = getResources();

        if (big)
        {
            v = R.drawable.vsibig;
            os = R.drawable.instrumentoutsidebig;
            vp = R.drawable.vsipointerbig;
        }
        else
        {
            v = R.drawable.vsi;
            os = R.drawable.instrumentoutside;
            vp = R.drawable.vsipointer;
        }

        vsiBitmap = BitmapFactory.decodeResource(res, v);
        outsideBitmap = BitmapFactory.decodeResource(res, os);
        pointerBitmap = BitmapFactory.decodeResource(res, vp);
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
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

        boolean negative = true;
        if (speed<0)
        {
            negative = false;
            speed = -speed;
        }
        double angle = ((double)speed) / 0.104d;
        if (negative) angle = -angle;

        // Create blank bitmap of equal size
        Bitmap rotateBitmap = Bitmap.createBitmap(pointerBitmap.getWidth(), pointerBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas pointerCanvas = new Canvas(rotateBitmap);
        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate((float)angle, pointerBitmap.getWidth()/2, pointerBitmap.getHeight()/2);
        pointerCanvas.drawBitmap(pointerBitmap, rotateMatrix, null);


        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(outsideBitmap, 0, 0, null);
        canvas.drawBitmap(vsiBitmap, (float)midx-(rotateBitmap.getWidth()/2), (float)midy-(rotateBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateBitmap, (float)midx-(pointerBitmap.getWidth()/2), (float)midy-(pointerBitmap.getHeight()/2), null);
    }
}
